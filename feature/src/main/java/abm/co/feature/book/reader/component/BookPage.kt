package abm.co.feature.book.reader.component

import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.widget.LoadingView
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.book.detailed.utils.ChapterWithTitle
import abm.co.feature.book.detailed.utils.PageSplitter
import abm.co.feature.book.detailed.utils.setSelectableTranslationText
import abm.co.feature.book.detailed.utils.toHtml
import abm.co.feature.book.detailed.utils.toImageGetter
import abm.co.feature.book.reader.BookReaderContract
import abm.co.feature.book.reader.BookReaderViewModel
import abm.co.feature.book.reader.model.ChapterEntityUI
import abm.co.feature.book.reader.model.ImageEntityUI
import android.text.TextPaint
import android.widget.TextView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.abs

val HORIZONTAL_PADDING = 16.dp

@Composable
fun BookPage(
    viewModel: BookReaderViewModel = viewModel()
) {
    SetStatusBarColor()
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    when (val state = uiState.state) {
        BookReaderContract.State.Loading -> {
            LoadingView(
                modifier = Modifier.fillMaxSize()
            )
        }

        is BookReaderContract.State.Success -> {
            BookScreen(
                defaultTitle = uiState.book.name,
                chapters = state.chapters,
                images = state.images,
                textPaint = state.textPaint,
                onBack = {

                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BookScreen(
    defaultTitle: String,
    chapters: ImmutableList<ChapterEntityUI>?,
    images: ImmutableList<ImageEntityUI>,
    textPaint: TextPaint,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState()
        Column(modifier = Modifier.matchParentSize()) {
            if (!chapters.isNullOrEmpty()) {
                MeasureUnconstrainedViewHeight(
                    modifier = Modifier.fillMaxSize(),
                    viewToMeasure = {
                        Column {
                            ChapterNameContainer("SUBCOMPOSE")
                            PageNumberContainer(0, 100)
                        }
                    },
                    content = { maxHeight, maxWidth ->
                        val pages = rememberBookPages(
                            defaultTitle = defaultTitle,
                            chapters = chapters,
                            textPaint = textPaint,
                            textViewSize = IntSize(maxWidth, maxHeight),
                            images = images
                        )
                        BookContent(
                            pages = pages,
                            images = images,
                            pagerState = pagerState,
                            textPaint = textPaint,
                            onClickWord = { word ->
                                println(word)
                            }
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BookContent(
    pagerState: PagerState = rememberPagerState(),
    pages: ImmutableList<ChapterWithTitle>,
    images: ImmutableList<ImageEntityUI>,
    textPaint: TextPaint,
    onClickWord: (String) -> Unit,
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
            ChapterNameContainer(title = currentPage.title)
            SingleScreenText(
                modifier = Modifier
                    .padding(horizontal = HORIZONTAL_PADDING)
                    .fillMaxWidth()
                    .weight(1f),
                chapterText = currentPage.chapterText,
                textPaint = textPaint,
                onClickWord = onClickWord,
                images = images
            )
            PageNumberContainer(pageNumber = index + 1, pagesCount = pages.size)
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
            pageHeight = textViewSize.height - with(density) { HORIZONTAL_PADDING.roundToPx() * 2 },
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
    images: ImmutableList<ImageEntityUI>,
    modifier: Modifier = Modifier,
    onClickWord: (String) -> Unit
) {
    val textColor = StudyCardsTheme.colors.textPrimary
    val htmlText = remember(chapterText, images) {
        chapterText.toHtml(imageGetter = images.toImageGetter())
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
private fun ChapterNameContainer(title: String) {
    Text(
        modifier = Modifier
            .statusBarsPadding()
            .padding(bottom = 10.dp, top = 5.dp)
            .fillMaxWidth(),
        text = title,
        style = StudyCardsTheme.typography.weight500Size16LineHeight20,
        color = StudyCardsTheme.colors.textSecondary,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun PageNumberContainer(
    pageNumber: Int,
    pagesCount: Int
) {
    Text(
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth(),
        text = "$pageNumber / $pagesCount",
        style = StudyCardsTheme.typography.weight500Size16LineHeight20,
        color = StudyCardsTheme.colors.textSecondary,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun MeasureUnconstrainedViewHeight(
    viewToMeasure: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (maxHeight: Int, maxWidth: Int) -> Unit
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val viewToMeasurePlaceable = subcompose(
            slotId = "viewToMeasure",
            content = viewToMeasure
        )[0].measure(constraints = Constraints(maxWidth = constraints.maxWidth))

        val maxHeight = abs(constraints.maxHeight - viewToMeasurePlaceable.height)
        val maxWidth = constraints.maxWidth

        val contentPlaceable = subcompose(
            slotId = "content",
            content = {
                content(maxHeight, maxWidth)
            }
        ).getOrNull(0)?.measure(constraints = constraints)
        layout(contentPlaceable?.width ?: 0, contentPlaceable?.height ?: 0) {
            contentPlaceable?.place(0, 0)
        }
    }
}