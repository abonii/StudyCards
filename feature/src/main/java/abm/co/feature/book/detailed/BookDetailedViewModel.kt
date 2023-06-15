package abm.co.feature.book.detailed

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.designsystem.message.snackbar.MessageType
import abm.co.domain.base.onFailure
import abm.co.domain.base.onSuccess
import abm.co.domain.repository.LibraryRepository
import abm.co.feature.R
import abm.co.feature.book.utils.downloadEpubFile
import abm.co.feature.book.utils.unzipEpubFile
import abm.co.feature.book.model.BookUI
import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class BookDetailedViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val libraryRepository: LibraryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val book: BookUI = savedStateHandle["book"]
        ?: throw NullPointerException("book must not be empty")

    private val _channel = Channel<BookDetailedContractChannel>()
    val channel: Flow<BookDetailedContractChannel> = _channel.receiveAsFlow()

    private val _state = MutableStateFlow(BookDetailedContractState(book))
    val state: StateFlow<BookDetailedContractState> = _state.asStateFlow()

    fun onEvent(event: BookDetailedContractEvent) {
        when (event) {
            is BookDetailedContractEvent.OnClickRead -> {
                downloadOrOpenBook()
            }

            BookDetailedContractEvent.OnBack -> {
                _channel.trySend(BookDetailedContractChannel.NavigateBack)
            }
        }
    }

    private fun downloadOrOpenBook() {
        viewModelScope.launch {
            val bookEntity = libraryRepository.getBook(book.name)
            if(bookEntity != null) {
                _channel.send(
                    BookDetailedContractChannel.NavigateToBookReader(
                        book = book,
                        bookUrl = bookEntity.url
                    )
                )
            } else {
                getEpubFromLink()
            }
        }
    }

    private fun getEpubFromLink() {
        viewModelScope.launch {
            downloadEpubFile(
                context = applicationContext,
                url = book.link,
                bookTitle = book.name
            )
        }
    }

    fun onFileReceived(file: File?) {
        viewModelScope.launch {
            if (file == null) {
                errorWhileGettingFile()
                return@launch
            }
            unzipEpubFile(
                bookTitle = book.name,
                epubFile = file
            ).onFailure {
                _channel.send(BookDetailedContractChannel.ShowMessage(it.toMessageContent()))
            }.onSuccess {
                val (bookEntity, chapters, images) = it
                libraryRepository.insertBook(bookEntity)
                libraryRepository.insertChapters(chapters)
                libraryRepository.insertImages(images)
                _channel.send(
                    BookDetailedContractChannel.NavigateToBookReader(
                        book = book,
                        bookUrl = bookEntity.url
                    )
                )
            }
        }
    }

    fun errorWhileGettingFile() {
        viewModelScope.launch {
            _channel.send(
                BookDetailedContractChannel.ShowMessage(
                    MessageContent.Snackbar.MessageContentRes(
                        titleRes = R.string.BookDetailed_Received_Error_title,
                        subtitleRes = R.string.BookDetailed_Received_Error_subtitle,
                        type = MessageType.Error
                    )
                )
            )
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
    data class NavigateToBookReader(val book: BookUI, val bookUrl: String) :
        BookDetailedContractChannel

    data class ShowMessage(val messageContent: MessageContent) : BookDetailedContractChannel
}