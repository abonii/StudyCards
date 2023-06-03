package abm.co.feature.book.detailed

import abm.co.designsystem.component.about.AboutView
import abm.co.designsystem.component.button.PrimaryButton
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.extensions.collectInLaunchedEffect
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.designsystem.toolbar.ToolbarState
import abm.co.designsystem.toolbar.rememberToolbarState
import abm.co.feature.R
import abm.co.feature.book.detailed.component.BookDetailedCollapsingToolbar
import abm.co.feature.book.model.BookUI
import abm.co.feature.userattributes.lanugage.defaultLanguages
import abm.co.feature.userattributes.lanugage.findByCode
import abm.co.feature.utils.AnalyticsManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun BookDetailedPage(
    navigateBack: () -> Unit,
    navigateToBookReader: (BookUI) -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: BookDetailedViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        AnalyticsManager.sendEvent(
            name = "book_detailed_page_viewed"
        )
    }
    viewModel.channel.collectInLaunchedEffect(function = { channel ->
        when (channel) {
            is BookDetailedContractChannel.NavigateToBookReader -> {
                navigateToBookReader(channel.book)
            }

            is BookDetailedContractChannel.ShowMessage -> {
                showMessage(channel.messageContent)
            }

            BookDetailedContractChannel.NavigateBack -> {
                navigateBack()
            }
        }
    })
    val state by viewModel.state.collectAsState()

    SetStatusBarColor(
        iconsColorsDark = false
    )
    Screen(
        uiState = state,
        onEvent = viewModel::onEvent
    )
}


@Composable
private fun Screen(
    uiState: BookDetailedContractState,
    onEvent: (BookDetailedContractEvent) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val minToolbarHeight = dimensionResource(id = abm.co.designsystem.R.dimen.default_200dp)
        val maxToolbarHeight = dimensionResource(id = abm.co.designsystem.R.dimen.default_325dp)
        val toolbarScrollable = rememberToolbarState(
            minHeight = minToolbarHeight,
            maxHeight = maxToolbarHeight
        )
        val scrollState = rememberScrollState()
        ListenToScrollAndUpdateToolbarState(
            scrollState = scrollState,
            toolbarState = toolbarScrollable
        )
        ScrollableContent(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(top = maxToolbarHeight)
                .fillMaxWidth(),
            onEvent = onEvent,
            uiState = uiState
        )
        BookDetailedCollapsingToolbar(
            progress = toolbarScrollable.progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(with(LocalDensity.current) { toolbarScrollable.height.toDp() })
                .graphicsLayer { translationY = toolbarScrollable.offset },
            bookImage = uiState.book.image,
            bookTitle = uiState.book.name,
            backgroundImage = uiState.book.bannerImage,
            onBack = {
                onEvent(BookDetailedContractEvent.OnBack)
            }
        )
    }
}

@Composable
private fun ScrollableContent(
    uiState: BookDetailedContractState,
    onEvent: (BookDetailedContractEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(10.dp))
        ParentContent(
            title = stringResource(id = R.string.BookDetailed_About_title),
            content = {
                AboutView(
                    details = uiState.book.description,
                    readAllText = stringResource(id = R.string.BookDetailed_About_expand),
                    collapseText = stringResource(id = R.string.BookDetailed_About_collapse),
                )
            }
        )
        Spacer(modifier = Modifier.height(15.dp))
        ParentContent(
            title = stringResource(id = R.string.BookDetailed_About_title),
            content = {
                ChildItem(
                    title = stringResource(id = R.string.BookDetailed_Info_langaugeTitle),
                    endContent = {
                        Text(
                            text = remember(uiState.book.languageCode) { uiState.book.languageCode.uppercase() },
                            style = StudyCardsTheme.typography.weight500Size14LineHeight20,
                            color = StudyCardsTheme.colors.textPrimary
                        )
                        defaultLanguages.findByCode(uiState.book.languageCode)?.let {
                            Spacer(modifier = Modifier.width(4.dp))
                            Image(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(id = it.flagFromDrawable),
                                contentDescription = null
                            )
                        }
                    }
                )
                ChildItem(
                    title = stringResource(id = R.string.BookDetailed_Info_levelTitle),
                    endContent = {
                        Text(
                            text = uiState.book.level.name,
                            style = StudyCardsTheme.typography.weight500Size14LineHeight20,
                            color = StudyCardsTheme.colors.textPrimary
                        )
                    }
                )
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        PrimaryButton(
            title = stringResource(id = R.string.BookDetailed_Button_readTitle),
            onClick = {
                onEvent(BookDetailedContractEvent.OnClickRead)
            },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(300.dp))
    }
}

@Composable
private fun ListenToScrollAndUpdateToolbarState(
    scrollState: ScrollState,
    toolbarState: ToolbarState
) {
    LaunchedEffect(scrollState.value) {
        toolbarState.scrollValue = scrollState.value
    }
}

@Composable
fun ParentContent(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            style = StudyCardsTheme.typography.weight600Size14LineHeight18,
            color = StudyCardsTheme.colors.textPrimary
        )
        content()
    }
}

@Composable
private fun ChildItem(
    title: String,
    endContent: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            text = title,
            style = StudyCardsTheme.typography.weight400Size14LineHeight24,
            color = StudyCardsTheme.colors.textPrimary
        )
        endContent()
    }
}
