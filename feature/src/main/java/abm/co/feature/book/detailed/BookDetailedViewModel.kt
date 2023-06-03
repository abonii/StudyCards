package abm.co.feature.book.detailed

import abm.co.designsystem.message.common.MessageContent
import abm.co.domain.repository.ServerRepository
import abm.co.feature.book.model.BookUI
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class BookDetailedViewModel @Inject constructor(
    private val serverRepository: ServerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val book: BookUI = savedStateHandle["book"]
        ?: throw NullPointerException("book must not be empty")

    private val _channel = Channel<BookDetailedContractChannel>()
    val channel: Flow<BookDetailedContractChannel> = _channel.receiveAsFlow()

    private val _state =
        MutableStateFlow<BookDetailedContractState>(BookDetailedContractState(book))
    val state: StateFlow<BookDetailedContractState> = _state.asStateFlow()

    fun onEvent(event: BookDetailedContractEvent) {
        when (event) {
            is BookDetailedContractEvent.OnClickRead -> {
                _channel.trySend(BookDetailedContractChannel.NavigateToBookReader(book))
            }

            BookDetailedContractEvent.OnBack -> {
                _channel.trySend(BookDetailedContractChannel.NavigateBack)
            }
        }
    }
}

@Stable
data class BookDetailedContractState(
    val book: BookUI
)

@Immutable
sealed interface BookDetailedContractEvent {
    object OnClickRead : BookDetailedContractEvent
    object OnBack : BookDetailedContractEvent
}

@Immutable
sealed interface BookDetailedContractChannel {
    object NavigateBack : BookDetailedContractChannel
    data class NavigateToBookReader(val book: BookUI) : BookDetailedContractChannel
    data class ShowMessage(val messageContent: MessageContent) : BookDetailedContractChannel
}