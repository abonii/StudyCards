package abm.co.feature.book.reader.component

import abm.co.designsystem.component.button.PrimaryButton
import abm.co.designsystem.component.button.TextButton
import abm.co.designsystem.component.dialog.NonDraggableBottomDialogView
import abm.co.designsystem.component.measure.MeasureUnconstrainedViewHeight
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.widget.LoadingView
import abm.co.designsystem.extensions.collectInLaunchedEffect
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.book.reader.BookReaderContract
import abm.co.feature.book.reader.BookReaderContractChannel
import abm.co.feature.book.reader.BookReaderContractDialog
import abm.co.feature.book.reader.BookReaderContractEvent
import abm.co.feature.book.reader.BookReaderViewModel
import abm.co.feature.book.reader.model.ChapterEntityUI
import abm.co.feature.book.reader.model.ImageEntityUI
import abm.co.feature.book.utils.ChapterWithTitle
import abm.co.feature.book.utils.PageSplitter
import abm.co.feature.book.utils.setSelectableTranslationText
import abm.co.feature.book.utils.toHtml
import abm.co.feature.card.model.CardUI
import abm.co.feature.card.wordinfo.WordInfoContractEvent
import abm.co.feature.card.wordinfo.WordInfoContractState
import abm.co.feature.card.wordinfo.WordInfoScreen
import abm.co.feature.utils.StudyCardsConstants
import android.text.TextPaint
import android.widget.TextView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

val HORIZONTAL_PADDING = 16.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookReaderPage(
    onBack: () -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: BookReaderViewModel = viewModel()
) {
    SetStatusBarColor()
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState()

    viewModel.channel.collectInLaunchedEffect { channel ->
        when (channel) {
            BookReaderContractChannel.NavigateBack -> {
                onBack()
            }

            is BookReaderContractChannel.ShowMessage -> {
                showMessage(channel.messageContent)
            }
        }
    }
    BookScreen(
        uiState = uiState,
        dialogState = dialogState,
        pagerState = pagerState,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BookScreen(
    uiState: BookReaderContract,
    dialogState: BookReaderContractDialog,
    pagerState: PagerState,
    onEvent: (BookReaderContractEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .background(StudyCardsTheme.colors.backgroundPrimary)
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        when (val state = uiState.state) {
            BookReaderContract.State.Loading -> {
                LoadingView(
                    modifier = Modifier.fillMaxSize()
                )
            }

            is BookReaderContract.State.Success -> {
                BookContent(
                    defaultTitle = uiState.book.name,
                    state = state,
                    pagerState = pagerState,
                    onEvent = onEvent,
                    learningWordsState = dialogState.learningWords
                )
            }
        }
        DialogContent(
            dialogState = dialogState,
            defaultTitle = uiState.book.name,
            onEvent = onEvent
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BookContent(
    defaultTitle: String,
    state: BookReaderContract.State.Success,
    learningWordsState: BookReaderContractDialog.LearningWords,
    pagerState: PagerState,
    onEvent: (BookReaderContractEvent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        var toolbarTitle by remember {
            mutableStateOf(defaultTitle)
        }
        var pageNumber by remember {
            mutableStateOf(1)
        }
        var maxPageNumber by remember {
            mutableStateOf(1)
        }
        Toolbar(
            title = toolbarTitle,
            onBack = {
                onEvent(BookReaderContractEvent.OnBack)
            },
            onClickLearningWords = {
                onEvent(BookReaderContractEvent.OnClickLearningWords)
            }
        )
        BoxWithConstraints(modifier = Modifier.weight(1f)) {
            val density = LocalDensity.current
            val pages = rememberBookPages(
                defaultTitle = defaultTitle,
                chapters = state.chapters,
                textPaint = state.textPaint,
                textViewSize = with(density) {
                    IntSize(
                        maxWidth.roundToPx(),
                        maxHeight.roundToPx()
                    )
                },
                images = state.images
            )
            BookContent(
                pages = pages,
                pagerState = pagerState,
                textPaint = state.textPaint,
                onEvent = onEvent
            )
            LaunchedEffect(pages, pagerState) {
                if(pages.size > 0) {
                    state.navigatorToPage.onEach { (chapterUrl, page) ->
                        val indexOfFirst = pages.indexOfFirst {
                            it.url == chapterUrl
                        }
                        if(indexOfFirst != -1){
                            delay(200)
                            pagerState.scrollToPage((indexOfFirst + page).coerceAtMost(pages.lastIndex))
                        }
                    }.launchIn(this)
                }
            }
            LaunchedEffect(pagerState, pages) {
                snapshotFlow {
                    pagerState.currentPage
                }.filter { it > 0 }.onEach { position ->
                    pageNumber = position + 1
                    if (pages.size > position) {
                        toolbarTitle = pages[position].title
                    }
                    val chapter = pages.getOrNull(pageNumber) ?: return@onEach
                    val indexOfFirst = pages.indexOfFirst {
                        it.url == chapter.url
                    }
                    onEvent(
                        BookReaderContractEvent.OnFlipBookPage(
                            chapterUrl = chapter.url,
                            page = position - indexOfFirst
                        )
                    )
                }.launchIn(this)
            }
            LaunchedEffect(pages) {
                snapshotFlow {
                    pages.size
                }.onEach {
                    maxPageNumber = it
                }.launchIn(this)
            }
            LearningWordsDialog(
                modifier = Modifier.align(Alignment.TopEnd),
                visible = learningWordsState.visible,
                words = learningWordsState.learningWords
            )
        }
        PageNumberContainer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 6.dp),
            pageNumber = pageNumber,
            pagesCount = maxPageNumber,
            onClickChapterContents = {
                onEvent(BookReaderContractEvent.OnClickOpenChapters)
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BookContent(
    pagerState: PagerState = rememberPagerState(),
    pages: ImmutableList<ChapterWithTitle>,
    textPaint: TextPaint,
    onEvent: (BookReaderContractEvent) -> Unit,
) {
    HorizontalPager(
        modifier = Modifier,
        pageCount = pages.size,
        state = pagerState
    ) { index ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            val currentPage = remember(pages) { pages[index] }
            SingleScreenText(
                modifier = Modifier
                    .padding(horizontal = HORIZONTAL_PADDING)
                    .fillMaxWidth()
                    .weight(1f),
                chapterText = currentPage.chapterText,
                textPaint = textPaint,
                onClickWord = {
                    onEvent(BookReaderContractEvent.OnClickWord(it))
                }
            )
        }
    }
}

@Composable
private fun rememberBookPages(
    defaultTitle: String,
    chapters: ImmutableList<ChapterEntityUI>,
    textPaint: TextPaint,
    textViewSize: IntSize,
    images: ImmutableList<ImageEntityUI>
): ImmutableList<ChapterWithTitle> {
    var list by remember { mutableStateOf<ImmutableList<ChapterWithTitle>>(persistentListOf()) }
    val density = LocalDensity.current
    LaunchedEffect(chapters) {
        val pageSplitter = PageSplitter(
            pageWidth = textViewSize.width - with(density) { HORIZONTAL_PADDING.roundToPx() * 2 },
            pageHeight = textViewSize.height - with(density) { HORIZONTAL_PADDING.roundToPx() },
            images = images,
            textPaint = textPaint
        )
        chapters.forEach {
            pageSplitter.append(
                title = it.title ?: defaultTitle,
                url = it.url,
                text = it.body
            )
        }
        list = pageSplitter.getPages().toImmutableList()
    }
    return list
}

@Composable
private fun SingleScreenText(
    chapterText: CharSequence,
    textPaint: TextPaint,
    modifier: Modifier = Modifier,
    onClickWord: (String) -> Unit
) {
    val textColor = StudyCardsTheme.colors.textPrimary
    val htmlText = remember(chapterText) {
        chapterText.toHtml()
    }
    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier
                .matchParentSize()
                .verticalScroll(rememberScrollState()),
            factory = {
                TextView(it).apply {
                    paint.textSize = textPaint.textSize
                    paint.typeface = textPaint.typeface
                }
            },
            update = { textView ->
                textView.setSelectableTranslationText(
                    onWordClick = onClickWord,
                    wholeText = htmlText,
                    color = textColor
                )
            }
        )
    }
}

@Composable
private fun Toolbar(
    title: String,
    onBack: () -> Unit,
    onClickLearningWords: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(StudyCardsConstants.TOOLBAR_HEIGHT)
            .fillMaxWidth()
            .padding(start = 6.dp, top = 6.dp, bottom = 6.dp, end = 6.dp)
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickableWithoutRipple(onBack)
                .padding(10.dp)
                .size(24.dp),
            painter = painterResource(id = R.drawable.ic_left),
            tint = StudyCardsTheme.colors.opposition,
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 10.dp, top = 5.dp)
                .fillMaxWidth(),
            text = title,
            style = StudyCardsTheme.typography.weight500Size16LineHeight20,
            color = StudyCardsTheme.colors.textSecondary,
            textAlign = TextAlign.Center
        )
        Icon(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickableWithoutRipple(onClickLearningWords)
                .padding(10.dp)
                .size(24.dp),
            painter = painterResource(id = R.drawable.ic_page),
            contentDescription = null
        )
    }
}

@Composable
private fun PageNumberContainer(
    pageNumber: Int,
    pagesCount: Int,
    onClickChapterContents: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(top = 12.dp),
            text = "$pageNumber / $pagesCount",
            style = StudyCardsTheme.typography.weight500Size14LineHeight20,
            color = StudyCardsTheme.colors.skyBlue
        )
        TextButton(
            modifier = Modifier
                .align(Alignment.CenterEnd),
            title = "^ Content",
            onClick = onClickChapterContents
        )
    }
}

@Composable
private fun DialogContent(
    dialogState: BookReaderContractDialog,
    onEvent: (BookReaderContractEvent.Dialog) -> Unit,
    defaultTitle: String
) {
    NonDraggableBottomDialogView(
        onDismiss = {
            onEvent(BookReaderContractEvent.Dialog.Chapter.OnDismiss)
        },
        content = {
            ChaptersDialog(
                defaultTitle = defaultTitle,
                chapters = dialogState.chapters.chapters,
                onClick = {
                    onEvent(BookReaderContractEvent.Dialog.Chapter.OnClickItem(it))
                },
                onClose = {
                    onEvent(BookReaderContractEvent.Dialog.Chapter.OnDismiss)
                }
            )
        },
        visible = dialogState.chapters.visible
    )
    val wordInfo = dialogState.wordInfo
    NonDraggableBottomDialogView(
        visible = wordInfo != null,
        onDismiss = {
            onEvent(BookReaderContractEvent.Dialog.WordInfo.OnDismiss)
        },
        content = {
            wordInfo?.let {
                WordInfoDialog(
                    wordInfo = wordInfo,
                    onEvent = onEvent
                )
            }
        }
    )
}

@Composable
private fun DialogToolbar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null
) {
    Box(
        modifier = modifier
            .height(StudyCardsConstants.TOOLBAR_HEIGHT)
            .fillMaxWidth()
            .padding(6.dp)
    ) {
        title?.let {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = it,
                style = StudyCardsTheme.typography.weight500Size16LineHeight20,
                color = StudyCardsTheme.colors.textSecondary,
            )
        }
        Icon(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickableWithoutRipple(onBack)
                .padding(10.dp)
                .size(24.dp),
            painter = painterResource(id = R.drawable.ic_close),
            tint = StudyCardsTheme.colors.opposition,
            contentDescription = null
        )
    }
}

@Composable
private fun LearningWordsDialog(
    visible: Boolean,
    words: ImmutableList<CardUI>,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        modifier = modifier.padding(horizontal = 10.dp),
        visible = visible
    ) {
        @Composable
        fun Item(item: CardUI) {
            Row(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.translation,
                    style = StudyCardsTheme.typography.weight400Size12LineHeight16,
                    color = StudyCardsTheme.colors.textPrimary,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = " - ",
                    style = StudyCardsTheme.typography.weight500Size16LineHeight20,
                    color = StudyCardsTheme.colors.textPrimary,
                    textAlign = TextAlign.End
                )
                Text(
                    text = item.name,
                    style = StudyCardsTheme.typography.weight400Size12LineHeight16,
                    color = StudyCardsTheme.colors.textPrimary,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Column(
            modifier = Modifier
                .heightIn(min = 220.dp, max = 360.dp)
                .width(220.dp)
                .background(
                    color = StudyCardsTheme.colors.backgroundSecondary,
                    shape = RoundedCornerShape(16.dp)
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            words.forEach {
                Item(item = it)
            }
        }
    }
}

@Composable
private fun WordInfoDialog(
    wordInfo: BookReaderContractDialog.WordInfo,
    onEvent: (BookReaderContractEvent.Dialog.WordInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(70.dp))
        Column(
            modifier = Modifier
                .background(
                    color = StudyCardsTheme.colors.backgroundPrimary,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .animateContentSize()
        ) {
            when (val state = wordInfo.state) {
                BookReaderContractDialog.WordInfo.State.Loading -> {
                    DialogToolbar(
                        onBack = {
                            onEvent(BookReaderContractEvent.Dialog.WordInfo.OnDismiss)
                        }
                    )
                    LoadingView(
                        modifier = Modifier
                            .height(200.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }

                is BookReaderContractDialog.WordInfo.State.SuccessOxford -> {
                    MeasureUnconstrainedViewHeight(
                        viewToMeasure = {
                            PrimaryButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 10.dp, bottom = 24.dp),
                                title = stringResource(id = R.string.BookReader_WordInfo_Action_learn),
                                onClick = {
                                    onEvent(BookReaderContractEvent.Dialog.WordInfo.OnSave)
                                }
                            )
                        }
                    ) { maxHeight, _ ->
                        Box {
                            WordInfoScreen(
                                checkedItemsID = wordInfo.checkedItemsID,
                                uiState = WordInfoContractState(
                                    oxfordResponse = state.response
                                ),
                                onEvent = { event ->
                                    when (event) {
                                        WordInfoContractEvent.OnBack -> {
                                            onEvent(BookReaderContractEvent.Dialog.WordInfo.OnDismiss)
                                        }

                                        is WordInfoContractEvent.OnClickEntry -> {
                                            onEvent(
                                                BookReaderContractEvent.Dialog.WordInfo.OnSelectOxfordItem(
                                                    event.value
                                                )
                                            )
                                        }

                                        is WordInfoContractEvent.OnClickPlayText -> {
                                            onEvent(
                                                BookReaderContractEvent.Dialog.WordInfo.OnClickPlayText(
                                                    event.value
                                                )
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier.padding(bottom = maxHeight)
                            )
                            PrimaryButton(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 10.dp, bottom = 24.dp),
                                title = stringResource(id = R.string.BookReader_WordInfo_Action_learn),
                                onClick = {
                                    onEvent(BookReaderContractEvent.Dialog.WordInfo.OnSave)
                                }
                            )
                        }
                    }
                }

                is BookReaderContractDialog.WordInfo.State.SuccessYandex -> {
                    DialogToolbar(
                        title = wordInfo.title,
                        onBack = {
                            onEvent(BookReaderContractEvent.Dialog.WordInfo.OnDismiss)
                        }
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = state.text,
                        style = StudyCardsTheme.typography.weight500Size16LineHeight20,
                        color = StudyCardsTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    PrimaryButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        title = stringResource(id = R.string.BookReader_WordInfo_Action_learn),
                        onClick = {
                            onEvent(BookReaderContractEvent.Dialog.WordInfo.OnSave)
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun ChaptersDialog(
    defaultTitle: String,
    chapters: ImmutableList<ChapterEntityUI>,
    onClick: (ChapterEntityUI) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    @Composable
    fun Item(item: ChapterEntityUI) {
        Box(
            modifier = Modifier
                .clickable(
                    onClick = {
                        onClick(item)
                    }
                )
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 16.dp)
        ) {
            Text(
                text = item.title ?: defaultTitle,
                style = StudyCardsTheme.typography.weight500Size16LineHeight20,
                color = StudyCardsTheme.colors.textPrimary
            )
        }
    }
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(70.dp))
        Column(
            modifier = Modifier
                .background(
                    color = StudyCardsTheme.colors.backgroundPrimary,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
        ) {
            DialogToolbar(
                title = "Chapters",
                onBack = onClose
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                chapters.forEach { chapter ->
                    Item(item = chapter)
                }
            }
        }
    }
}
