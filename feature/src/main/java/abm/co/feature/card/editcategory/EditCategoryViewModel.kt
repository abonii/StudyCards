package abm.co.feature.card.editcategory

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
class EditCategoryViewModel @Inject constructor(
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val _channel = Channel<EditCategoryContractChannel>()
    val channel = _channel.receiveAsFlow()

    private val _state = MutableStateFlow(
        EditCategoryContractState(
            progress = 0.3f
        )
    )
    val state: StateFlow<EditCategoryContractState> = _state.asStateFlow()

    init {
        fetchCategories()
    }

    fun onEvent(event: EditCategoryContractEvent) {
        when (event) {
            is EditCategoryContractEvent.OnEnterCategoryName -> {
                _state.update {
                    it.copy(categoryName = event.value)
                }
            }

            EditCategoryContractEvent.OnContinue -> {
                onContinueButtonClicked()
            }

            EditCategoryContractEvent.OnBackClicked -> {
                viewModelScope.launch {
                    _channel.send(EditCategoryContractChannel.NavigateBack)
                }
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            serverRepository.getCategories.collectLatest { either ->
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
                name = state.value.categoryName,
                cardsCount = 0,
                bookmarked = false,
                creatorName = null,
                creatorID = null,
                imageURL = null,
                id = ""
            )
            val newCategory = serverRepository.createCategory(category.toDomain())
            newCategory.onSuccess {
                _channel.send(EditCategoryContractChannel.NavigateToNewCard(category = it.toUI()))
            }.onFailure {
                it.sendException()
            }
        }
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent()?.let {
                _channel.send(EditCategoryContractChannel.ShowMessage(it))
            }
        }
    }
}

@Immutable
data class EditCategoryContractState(
    val progress: Float?, // 0..1
    val categoryName: String = "",
    val categories: List<CategoryUI> = emptyList()
)

@Immutable
sealed interface EditCategoryContractEvent {
    data class OnEnterCategoryName(val value: String) : EditCategoryContractEvent
    object OnContinue : EditCategoryContractEvent
    object OnBackClicked : EditCategoryContractEvent
}

@Immutable
sealed interface EditCategoryContractChannel {
    object NavigateBack : EditCategoryContractChannel
    data class NavigateToNewCard(val category: CategoryUI) : EditCategoryContractChannel
    data class ShowMessage(val messageContent: MessageContent) : EditCategoryContractChannel
}