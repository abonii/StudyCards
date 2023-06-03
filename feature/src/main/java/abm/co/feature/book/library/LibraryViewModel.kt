package abm.co.feature.book.library

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.domain.base.onFailure
import abm.co.domain.base.onSuccess
import abm.co.domain.repository.ServerRepository
import abm.co.feature.book.model.BookUI
import abm.co.feature.book.model.toUI
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val _channel = Channel<LibraryContractChannel>()
    val channel: Flow<LibraryContractChannel> = _channel.receiveAsFlow()

    private val _state = MutableStateFlow<LibraryContractState>(LibraryContractState.Loading)
    val state: StateFlow<LibraryContractState> = _state.asStateFlow()

    init {
        fetchLibrary()
    }

    fun onEvent(event: LibraryContractEvent) {
        when (event) {
            is LibraryContractEvent.OnClickBook -> {
                _channel.trySend(LibraryContractChannel.NavigateToBookInfo(event.book))
            }
        }
    }

    private fun fetchLibrary() {
        serverRepository.getLibrary
            .onEach { either ->
                either.onFailure { failure ->
                    failure.toMessageContent()?.let {
                        _channel.send(LibraryContractChannel.ShowMessage(it))
                    }
                }.onSuccess { books ->
                    println(books)
                    _state.value = LibraryContractState.Success(books.map { it.toUI() })
                }
            }
            .launchIn(viewModelScope)
    }
}

@Stable
sealed interface LibraryContractState {
    object Loading : LibraryContractState
    data class Success(val books: List<BookUI>) : LibraryContractState
}

@Immutable
sealed interface LibraryContractEvent {
    data class OnClickBook(val book: BookUI): LibraryContractEvent
}

@Immutable
sealed interface LibraryContractChannel {
    data class NavigateToBookInfo(val book: BookUI): LibraryContractChannel
    data class ShowMessage(val messageContent: MessageContent) : LibraryContractChannel
}