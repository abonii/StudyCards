package abm.co.feature.book.reader

import abm.co.data.model.oxford.EMPTY_TRANSLATION
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.designsystem.message.snackbar.MessageType
import abm.co.domain.base.Failure
import abm.co.domain.base.onFailure
import abm.co.domain.base.onLeft
import abm.co.domain.base.onRight
import abm.co.domain.base.onSuccess
import abm.co.domain.model.LastOpenedBookPage
import abm.co.domain.repository.LibraryRepository
import abm.co.domain.repository.ServerRepository
import abm.co.domain.usecase.GetWordInfoUseCase
import abm.co.feature.R
import abm.co.feature.book.model.BookUI
import abm.co.feature.book.reader.model.ChapterEntityUI
import abm.co.feature.book.reader.model.ImageEntityUI
import abm.co.feature.book.reader.model.toUI
import abm.co.feature.card.model.CardKindUI
import abm.co.feature.card.model.CardUI
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.card.model.OxfordEntryUI
import abm.co.feature.card.model.OxfordTranslationResponseUI
import abm.co.feature.card.model.toDomain
import abm.co.feature.card.model.toUI
import abm.co.feature.utils.TextToSpeechManager
import android.content.Context
import android.text.TextPaint
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class BookReaderViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val libraryRepository: LibraryRepository,
    private val getWordInfoUseCase: GetWordInfoUseCase,
    private val textToSpeechManager: TextToSpeechManager,
    private val serverRepository: ServerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val book: BookUI = savedStateHandle["book"]
        ?: throw NullPointerException("book must not be empty")

    private val bookUrl: String = savedStateHandle["book_url"]
        ?: throw NullPointerException("book_url must not be empty")

    private val _channel = Channel<BookReaderContractChannel>()
    val channel: Flow<BookReaderContractChannel> = _channel.receiveAsFlow()

    private val checkedItemsID: SnapshotStateList<String> = mutableStateListOf()

    private val _navigatorToPage = Channel<Pair<String, Int>>()
    private val navigatorToPage: Flow<Pair<String, Int>> = _navigatorToPage.receiveAsFlow()

    private val _uiState = MutableStateFlow(
        BookReaderContract(
            book = book,
            state = BookReaderContract.State.Loading
        )
    )
    val state: StateFlow<BookReaderContract> = _uiState.asStateFlow()

    private val _dialogState = MutableStateFlow(BookReaderContractDialog())
    val dialogState: StateFlow<BookReaderContractDialog> = _dialogState.asStateFlow()

    init {
        getBook()
        fetchLearningWords()
    }

    fun onEvent(event: BookReaderContractEvent) {
        when (event) {
            BookReaderContractEvent.OnBack -> {
                _channel.trySend(BookReaderContractChannel.NavigateBack)
            }

            BookReaderContractEvent.OnClickOpenChapters -> {
                _dialogState.update { oldState ->
                    oldState.copy(chapters = oldState.chapters.copy(visible = true))
                }
            }

            BookReaderContractEvent.OnClickLearningWords -> {
                _dialogState.update { oldState ->
                    oldState.copy(
                        learningWords = oldState.learningWords.copy(
                            visible = !oldState.learningWords.visible
                        )
                    )
                }
            }

            is BookReaderContractEvent.OnClickWord -> {
                val isOpenLearningWords = dialogState.value.learningWords.visible
                if (isOpenLearningWords) {
                    _dialogState.update { oldState ->
                        oldState.copy(
                            learningWords = oldState.learningWords.copy(
                                visible = false
                            )
                        )
                    }
                } else {
                    onClickWord(word = event.word)
                }
            }

            is BookReaderContractEvent.Dialog.Chapter.OnClickItem -> {
                navigateToPage(event.item)
            }

            BookReaderContractEvent.Dialog.Chapter.OnDismiss -> {
                _dialogState.update { oldState ->
                    oldState.copy(chapters = oldState.chapters.copy(visible = false))
                }
            }

            BookReaderContractEvent.Dialog.LearningWord.OnDismiss -> {
                _dialogState.update { oldState ->
                    oldState.copy(
                        learningWords = oldState.learningWords.copy(visible = false)
                    )
                }
            }

            BookReaderContractEvent.Dialog.WordInfo.OnDismiss -> {
                checkedItemsID.clear()
                _dialogState.update { oldState ->
                    oldState.copy(
                        wordInfo = null
                    )
                }
            }

            BookReaderContractEvent.Dialog.WordInfo.OnSave -> {
                onSave()
            }

            is BookReaderContractEvent.Dialog.WordInfo.OnSelectOxfordItem -> {
                if (checkedItemsID.contains(event.value.id)) {
                    checkedItemsID.remove(event.value.id)
                } else {
                    checkedItemsID.add(event.value.id)
                }
            }

            is BookReaderContractEvent.Dialog.WordInfo.OnClickPlayText -> {
                speak(event.value)
            }

            is BookReaderContractEvent.OnFlipBookPage -> {
                saveLastOpenedBookPage(chapterUrl = event.chapterUrl, page = event.page)
            }
        }
    }

    private fun fetchLearningWords() {
        serverRepository.getUserCards(
            book.id
        ).onEach { either ->
            either.onSuccess {
                _dialogState.update { oldState ->
                    oldState.copy(
                        learningWords = oldState.learningWords.copy(
                            learningWords = it.map { it.toUI() }.toImmutableList()
                        )
                    )
                }
            }.onFailure {
                it.sendException()
            }
        }.launchIn(viewModelScope)
    }

    private fun getBook() {
        viewModelScope.launch {
            val chapters = libraryRepository.getChapters(bookUrl)
            val images = libraryRepository.getImages(bookUrl)
            _dialogState.update { oldState ->
                oldState.copy(
                    chapters = oldState.chapters.copy(
                        chapters = chapters.map { it.toUI() }.toImmutableList()
                    )
                )
            }
            _uiState.update { oldState ->
                oldState.copy(
                    state = BookReaderContract.State.Success(
                        chapters = chapters.map {
                            it.toUI()
                        }.filter {
                            it.body.isNotBlank() && !(it.body == "</br>" || it.body == "<br>")
                        }.toImmutableList(),
                        images = images.map { it.toUI() }.toImmutableList(),
                        textPaint = getTextPaint(),
                        navigatorToPage = navigatorToPage
                    )
                )
            }
            fetchLastOpenedPage()
        }
    }

    private suspend fun fetchLastOpenedPage() {
        val lastOpenedBookPage = libraryRepository.getLastOpenedBookPage(bookUrl) ?: return
        val chapter = (_uiState.value.state as? BookReaderContract.State.Success)?.chapters?.find {
            it.url == lastOpenedBookPage.chapterUrl
        } ?: return
        println("fetchLastOpenedPage: ${chapter.url} - $lastOpenedBookPage")
        _navigatorToPage.send(chapter.url to lastOpenedBookPage.page)
    }

    private fun getTextPaint(): TextPaint {
        return TextPaint().apply {
            val typeface = ResourcesCompat.getFont(
                applicationContext,
                abm.co.designsystem.R.font.golos_text_regular
            )
            setTypeface(typeface)
            textSize = with(Density(applicationContext)) { 25.dp.toPx() }
        }
    }

    private fun onClickWord(word: String, fromNative: Boolean = false) {
        viewModelScope.launch {
            _dialogState.update { oldState ->
                oldState.copy(
                    wordInfo = BookReaderContractDialog.WordInfo(
                        title = word,
                        state = BookReaderContractDialog.WordInfo.State.Loading,
                        checkedItemsID = checkedItemsID
                    )
                )
            }
            getWordInfoUseCase(word = word, fromNative = fromNative)
                .onFailure {
                    _channel.send(BookReaderContractChannel.ShowMessage(it.toMessageContent()))
                    _dialogState.update { oldState ->
                        oldState.copy(wordInfo = null)
                    }
                }
                .onSuccess { either ->
                    either.onLeft { response ->
                        _dialogState.update { oldState ->
                            oldState.copy(
                                wordInfo = BookReaderContractDialog.WordInfo(
                                    title = word,
                                    state = BookReaderContractDialog.WordInfo.State.SuccessOxford(
                                        response = response.toUI()
                                    ),
                                    checkedItemsID = checkedItemsID
                                )
                            )
                        }
                    }.onRight { response ->
                        _dialogState.update { oldState ->
                            oldState.copy(
                                wordInfo = BookReaderContractDialog.WordInfo(
                                    title = word,
                                    state = BookReaderContractDialog.WordInfo.State.SuccessYandex(
                                        text = response.text?.joinToString("; ") ?: ""
                                    ),
                                    checkedItemsID = checkedItemsID
                                )
                            )
                        }
                    }
                }
        }
    }

    /**
     * [page] starts counting from [item]
     **/
    private fun saveLastOpenedBookPage(chapterUrl: String, page: Int) {
        viewModelScope.launch {
            libraryRepository.setLastOpenedBookPage(
                LastOpenedBookPage(
                    bookUrl = bookUrl,
                    chapterUrl = chapterUrl,
                    page = page
                )
            )
        }
    }

    private fun navigateToPage(item: ChapterEntityUI) {
        viewModelScope.launch {
            _navigatorToPage.send(item.url to 0)
            delay(400)
            _dialogState.update { oldState ->
                oldState.copy(
                    chapters = oldState.chapters.copy(visible = false),
                    learningWords = oldState.learningWords.copy(visible = false)
                )
            }
            saveLastOpenedBookPage(
                chapterUrl = item.url,
                page = 0
            )
        }
    }

    private fun speak(value: String) {
        viewModelScope.launch {
            val canSpeak = textToSpeechManager.speakAndGet(value)
            if (!canSpeak) {
                _channel.send(
                    BookReaderContractChannel.ShowMessage(
                        MessageContent.Snackbar.MessageContentRes(
                            titleRes = abm.co.designsystem.R.string.Messages_oops,
                            subtitleRes = R.string.Category_Message_Error_TextToSpeech_notFound,
                            type = MessageType.Error
                        )
                    )
                )
            }
        }
    }

    private fun onSave() {
        viewModelScope.launch {
            when (val state = dialogState.value.wordInfo?.state) {
                is BookReaderContractDialog.WordInfo.State.SuccessOxford -> {
                    println("oxford: $checkedItemsID")
                    val categoryUI = CategoryUI(
                        title = book.name,
                        cardsCount = 0,
                        bookmarked = false,
                        creatorName = null,
                        creatorID = null,
                        imageURL = book.image,
                        id = book.id,
                        published = false
                    )
                    serverRepository.createUserCategory(
                        category = categoryUI.toDomain(),
                        id = book.id
                    ).onSuccess { category ->
                        val (translations, examples) = getTranslationsAndExamplesOxfordResponse(
                            oxfordResponse = state.response,
                            checkedOxfordItemsID = checkedItemsID.toTypedArray()
                        )
                        val cardUI = CardUI(
                            name = translations,
                            kind = CardKindUI.UNDEFINED,
                            translation = dialogState.value.wordInfo?.title ?: "",
                            imageUrl = "",
                            repeatedCount = 0,
                            example = examples ?: "",
                            categoryID = category.id,
                            nextRepeatTime = Calendar.getInstance().timeInMillis,
                            cardID = "",
                            learnedPercent = 0f
                        )
                        serverRepository.createUserCard(card = cardUI.toDomain())
                    }.onFailure {
                        it.sendException()
                    }
                }

                is BookReaderContractDialog.WordInfo.State.SuccessYandex -> {
                    val categoryUI = CategoryUI(
                        title = book.name,
                        cardsCount = 0,
                        bookmarked = false,
                        creatorName = null,
                        creatorID = null,
                        imageURL = book.image,
                        id = book.id,
                        published = false
                    )
                    serverRepository.createUserCategory(
                        category = categoryUI.toDomain(),
                        id = book.id
                    ).onSuccess { category ->
                        val cardUI = CardUI(
                            name = state.text,
                            kind = CardKindUI.UNDEFINED,
                            translation = dialogState.value.wordInfo?.title ?: "",
                            imageUrl = "",
                            repeatedCount = 0,
                            example = "",
                            categoryID = category.id,
                            nextRepeatTime = Calendar.getInstance().timeInMillis,
                            cardID = "",
                            learnedPercent = 0f
                        )
                        serverRepository.createUserCard(card = cardUI.toDomain())
                    }
                }

                else -> Unit
            }
            _dialogState.update { oldState ->
                oldState.copy(
                    wordInfo = null
                )
            }
            checkedItemsID.clear()
        }
    }

    private fun getTranslationsAndExamplesOxfordResponse(
        oxfordResponse: OxfordTranslationResponseUI?,
        checkedOxfordItemsID: Array<String>
    ): Pair<String, String?> {
        val entries = oxfordResponse?.lexicalEntry?.flatMap { lexicalEntryUI ->
            lexicalEntryUI.entries?.filter { checkedOxfordItemsID.contains(it.id) }
                ?: emptyList()
        }
        val translations = entries?.filter {
            it.translation != EMPTY_TRANSLATION
        }?.joinToString("\n") { entryUI ->
            entryUI.translation
        } ?: ""

        val examples = entries?.flatMap { entryUI ->
            entryUI.examples ?: emptyList()
        }?.mapIndexed { index, exampleUI ->
            (index + 1) to exampleUI
        }?.joinToString("") { "${it.first}. ${it.second.text} - ${it.second.translation}\n" }

        return translations to examples
    }

    private fun Failure.sendException() {
        viewModelScope.launch {
            this@sendException.toMessageContent().let {
                _channel.send(BookReaderContractChannel.ShowMessage(it))
            }
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
            val textPaint: TextPaint,
            val navigatorToPage: Flow<Pair<String, Int>>
        ) : State
    }
}

@Stable
data class BookReaderContractDialog(
    val wordInfo: WordInfo? = null,
    val chapters: Chapters = Chapters(),
    val learningWords: LearningWords = LearningWords()
) {
    @Stable
    data class LearningWords(
        val visible: Boolean = false,
        val learningWords: ImmutableList<CardUI> = persistentListOf()
    )

    @Stable
    data class Chapters(
        val visible: Boolean = false,
        val chapters: ImmutableList<ChapterEntityUI> = persistentListOf()
    )

    @Stable
    data class WordInfo(
        val title: String,
        val state: State,
        val checkedItemsID: SnapshotStateList<String>
    ) {
        @Stable
        sealed interface State {
            object Loading : State
            data class SuccessOxford(val response: OxfordTranslationResponseUI) : State
            data class SuccessYandex(val text: String) : State
        }
    }
}

@Immutable
sealed interface BookReaderContractEvent {
    object OnClickOpenChapters : BookReaderContractEvent
    object OnClickLearningWords : BookReaderContractEvent
    data class OnClickWord(val word: String) : BookReaderContractEvent
    object OnBack : BookReaderContractEvent
    data class OnFlipBookPage(
        val chapterUrl: String,
        val page: Int
    ): BookReaderContractEvent

    sealed interface Dialog : BookReaderContractEvent {
        sealed interface LearningWord : Dialog {
            object OnDismiss : LearningWord
        }

        sealed interface WordInfo : Dialog {
            object OnDismiss : WordInfo
            data class OnSelectOxfordItem(
                val value: OxfordEntryUI
            ) : WordInfo

            object OnSave : WordInfo
            data class OnClickPlayText(val value: String) : WordInfo
        }

        sealed interface Chapter : Dialog {
            object OnDismiss : Chapter
            data class OnClickItem(val item: ChapterEntityUI) : Chapter
        }
    }
}

@Immutable
sealed interface BookReaderContractChannel {
    object NavigateBack : BookReaderContractChannel
    data class ShowMessage(val messageContent: MessageContent) : BookReaderContractChannel
}
