package abm.co.feature.card.selectcategory

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.Failure
import abm.co.domain.base.onFailure
import abm.co.domain.base.onSuccess
import abm.co.domain.repository.ServerRepository
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.card.model.toUI
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectCategoryViewModel @Inject constructor(
    private val serverRepository: ServerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryID: String? = savedStateHandle["category_id"]

    private val _channel = Channel<SelectCategoryContractChannel>()
    val channel = _channel.receiveAsFlow()

    private val _state = MutableStateFlow<SelectCategoryContractState>(
        SelectCategoryContractState.Loading
    )
    val state: StateFlow<SelectCategoryContractState> = _state.asStateFlow()

    private val categoryList = mutableStateListOf<CategoryUI>()

    init {
        fetchCategories()
    }

    fun onEvent(event: SelectCategoryContractEvent) {
        when (event) {
            is SelectCategoryContractEvent.OnClickCategory -> {
                viewModelScope.launch {
                    _channel.send(
                        SelectCategoryContractChannel.OnCategorySelected(event.value)
                    )
                }
            }

            SelectCategoryContractEvent.OnBack -> {
                viewModelScope.launch {
                    _channel.send(
                        SelectCategoryContractChannel.NavigateBack
                    )
                }
            }
        }
    }

    private fun fetchCategories() {
        serverRepository.getUserCategories.onEach { setsOfCardsEither ->
            setsOfCardsEither.onFailure {
                it.sendException()
            }.onSuccess { categories ->
                categoryList.clear()
                categoryList.addAll(categories.map { it.toUI() })
                if (categories.isEmpty()) {
                    _state.value = SelectCategoryContractState.Empty
                } else {
                    _state.value = SelectCategoryContractState.Success(
                        categories = categoryList,
                        selectedCategoryID = categoryID
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent()?.let {
                _channel.send(SelectCategoryContractChannel.ShowMessage(it))
            }
        }
    }
}

@Stable
sealed interface SelectCategoryContractState {
    object Loading : SelectCategoryContractState

    object Empty : SelectCategoryContractState

    data class Success(
        val categories: List<CategoryUI>,
        val selectedCategoryID: String?
    ) : SelectCategoryContractState
}

@Immutable
sealed interface SelectCategoryContractEvent {
    object OnBack : SelectCategoryContractEvent
    data class OnClickCategory(val value: CategoryUI) : SelectCategoryContractEvent
}

@Immutable
sealed interface SelectCategoryContractChannel {
    object NavigateBack : SelectCategoryContractChannel
    data class OnCategorySelected(val value: CategoryUI) : SelectCategoryContractChannel
    data class ShowMessage(val messageContent: MessageContent) : SelectCategoryContractChannel
}
