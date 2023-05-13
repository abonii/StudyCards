package abm.co.feature.card.editcategory

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.Failure
import abm.co.domain.base.onFailure
import abm.co.domain.base.onSuccess
import abm.co.domain.repository.ServerRepository
import abm.co.feature.card.model.CategoryUI
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditCategoryViewModel @Inject constructor(
    private val serverRepository: ServerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val category: CategoryUI = savedStateHandle["category"]
        ?: throw RuntimeException("cannot be empty CATEGORY argument")

    private val _channel = Channel<EditCategoryContractChannel>()
    val channel = _channel.receiveAsFlow()

    private val _state = MutableStateFlow(
        EditCategoryContractState(
            title = category.title
        )
    )
    val state: StateFlow<EditCategoryContractState> = _state.asStateFlow()

    fun onEvent(event: EditCategoryContractEvent) {
        when (event) {
            EditCategoryContractEvent.OnBack -> {
                viewModelScope.launch {
                    _channel.send(
                        EditCategoryContractChannel.NavigateBack()
                    )
                }
            }

            EditCategoryContractEvent.OnClickSave -> {
                updateCategory()
            }

            is EditCategoryContractEvent.OnEnterTitle -> {
                _state.update { oldState ->
                    oldState.copy(title = event.value)
                }
            }
        }
    }

    private fun updateCategory(
        title: String = state.value.title
    ) {
        viewModelScope.launch {
            val newCategory = category.copy(
                title = title
            )
            serverRepository.updateUserCategory(
                id = category.id,
                name = title
            ).onFailure {
                it.sendException()
            }.onSuccess {
                _channel.send(
                    EditCategoryContractChannel.NavigateBack(newCategory)
                )
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

@Stable
data class EditCategoryContractState(
    val title: String = ""
)

@Immutable
sealed interface EditCategoryContractEvent {
    object OnBack : EditCategoryContractEvent
    object OnClickSave : EditCategoryContractEvent
    data class OnEnterTitle(val value: String) : EditCategoryContractEvent
}

@Immutable
sealed interface EditCategoryContractChannel {
    data class NavigateBack(val value: CategoryUI? = null) : EditCategoryContractChannel
    data class ShowMessage(val messageContent: MessageContent) : EditCategoryContractChannel
}
