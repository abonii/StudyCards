package abm.co.feature.book.reader

import abm.co.designsystem.message.common.MessageContent
import abm.co.domain.repository.LibraryRepository
import abm.co.feature.book.model.BookUI
import abm.co.feature.book.reader.model.ChapterEntityUI
import abm.co.feature.book.reader.model.ImageEntityUI
import abm.co.feature.book.reader.model.toUI
import android.content.Context
import android.text.TextPaint
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookReaderViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val libraryRepository: LibraryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val book: BookUI = savedStateHandle["book"]
        ?: throw NullPointerException("book must not be empty")

    private val bookUrl: String = savedStateHandle["book_url"]
        ?: throw NullPointerException("book_url must not be empty")

    private val _channel = Channel<BookReaderContractChannel>()
    val channel: Flow<BookReaderContractChannel> = _channel.receiveAsFlow()

    private val _uiState = MutableStateFlow(
        BookReaderContract(
            book = book,
            state = BookReaderContract.State.Loading
        )
    )
    val state: StateFlow<BookReaderContract> = _uiState.asStateFlow()

    init {
        getBook()
    }

    fun onEvent(event: BookReaderContractEvent) {
        when (event) {
            BookReaderContractEvent.OnBack -> {
                _channel.trySend(BookReaderContractChannel.NavigateBack)
            }

            BookReaderContractEvent.OnClickOpenChapters -> {

            }

            BookReaderContractEvent.OnClickOpenLearningWords -> {

            }

            is BookReaderContractEvent.OnClickWord -> {

            }
        }
    }

    private fun getBook() {
        viewModelScope.launch {
            val chapters = libraryRepository.getChapters(bookUrl)
            val images = libraryRepository.getImages(bookUrl)
            _uiState.update { oldState ->
                oldState.copy(
                    state = BookReaderContract.State.Success(
                        chapters = chapters.map { it.toUI() }.filter { it.body.isNotBlank() }.toImmutableList(),
                        images = images.map { it.toUI() }.toImmutableList(),
                        textPaint = getTextPaint()
                    )
                )
            }
        }
    }

    private fun getTextPaint(): TextPaint {
        return TextPaint().apply {
            val typeface = ResourcesCompat.getFont(
                applicationContext,
                abm.co.designsystem.R.font.golos_text_regular
            )
            setTypeface(typeface)
            textSize = 60f
        }
    }
}

@Stable
data class BookReaderContract(
    val book: BookUI,
    val state: State
) {
    @Stable
    sealed interface State {
        object Loading : State
        data class Success(
            val chapters: ImmutableList<ChapterEntityUI>,
            val images: ImmutableList<ImageEntityUI>,
            val textPaint: TextPaint
        ) : State
    }

    @Stable
    data class Dialog(
        val chapters: Chapters? = null
    ) {
        data class Chapters(
            val visible: Boolean = false,
            val chapters: ImmutableList<ChapterEntityUI>
        )
    }
}

@Immutable
sealed interface BookReaderContractEvent {
    object OnClickOpenChapters : BookReaderContractEvent
    object OnClickOpenLearningWords : BookReaderContractEvent
    data class OnClickWord(val word: String) : BookReaderContractEvent
    object OnBack : BookReaderContractEvent
}

@Immutable
sealed interface BookReaderContractChannel {
    object NavigateBack : BookReaderContractChannel
    data class ShowMessage(val messageContent: MessageContent) : BookReaderContractChannel
}