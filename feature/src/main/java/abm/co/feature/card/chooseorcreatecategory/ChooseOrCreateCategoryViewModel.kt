package abm.co.feature.card.chooseorcreatecategory

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.Failure
import abm.co.domain.base.onFailure
import abm.co.domain.base.onSuccess
import abm.co.domain.repository.ServerRepository
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.card.model.toDomain
import abm.co.feature.card.model.toUI
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseOrCreateCategoryViewModel @Inject constructor(
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val _channel = Channel<ChooseOrCreateCategoryContractChannel>()
    val channel = _channel.receiveAsFlow()

    private val _state = MutableStateFlow(
        ChooseOrCreateCategoryContractState(
            progress = 0.3f
        )
    )
    val state: StateFlow<ChooseOrCreateCategoryContractState> = _state.asStateFlow()

    init {
        fetchCategories()
    }

    fun onEvent(event: ChooseOrCreateCategoryContractEvent) {
        when (event) {
            is ChooseOrCreateCategoryContractEvent.OnEnterCategoryName -> {
                _state.update {
                    it.copy(categoryName = event.value)
                }
            }

            ChooseOrCreateCategoryContractEvent.OnContinue -> {
                onContinueButtonClicked()
            }

            ChooseOrCreateCategoryContractEvent.OnBackClicked -> {
                viewModelScope.launch {
                    _channel.send(ChooseOrCreateCategoryContractChannel.NavigateBack)
                }
            }

            is ChooseOrCreateCategoryContractEvent.OnCategoryClicked -> {
                viewModelScope.launch {
                    _channel.send(
                        ChooseOrCreateCategoryContractChannel.NavigateToNewCard(
                            category = event.item
                        )
                    )
                }
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            serverRepository.getUserCategories.collectLatest { either ->
                either.onFailure {
                    it.sendException()
                }
                either.onSuccess {
                    _state.update { oldState ->
                        oldState.copy(
                            categories = it.map { it.toUI() }
                        )
                    }
                }
            }
        }
    }

    private fun onContinueButtonClicked() {
        viewModelScope.launch {
            val category = CategoryUI(
                title = state.value.categoryName,
                cardsCount = 0,
                bookmarked = false,
                creatorName = null,
                creatorID = null,
                imageURL = null,
                id = "",
                published = false
            )
            val newCategory = serverRepository.createUserCategory(category.toDomain())
            newCategory.onSuccess {
                _channel.send(ChooseOrCreateCategoryContractChannel.NavigateToNewCard(category = it.toUI()))
            }.onFailure {
                it.sendException()
            }
        }
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent()?.let {
                _channel.send(ChooseOrCreateCategoryContractChannel.ShowMessage(it))
            }
        }
    }
}

@Immutable
data class ChooseOrCreateCategoryContractState(
    val progress: Float?, // 0..1
    val categoryName: String = "",
    val categories: List<CategoryUI> = emptyList()
)

@Immutable
sealed interface ChooseOrCreateCategoryContractEvent {
    data class OnEnterCategoryName(val value: String) : ChooseOrCreateCategoryContractEvent
    object OnContinue : ChooseOrCreateCategoryContractEvent
    object OnBackClicked : ChooseOrCreateCategoryContractEvent
    data class OnCategoryClicked(val item: CategoryUI) : ChooseOrCreateCategoryContractEvent
}

@Immutable
sealed interface ChooseOrCreateCategoryContractChannel {
    object NavigateBack : ChooseOrCreateCategoryContractChannel
    data class NavigateToNewCard(val category: CategoryUI) : ChooseOrCreateCategoryContractChannel
    data class ShowMessage(val messageContent: MessageContent) : ChooseOrCreateCategoryContractChannel
}