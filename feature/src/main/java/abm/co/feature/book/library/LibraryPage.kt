package abm.co.feature.book.library

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.baseBackground
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.widget.LoadingView
import abm.co.designsystem.extensions.collectInLaunchedEffect
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.book.library.component.LibraryKindTitle
import abm.co.feature.book.library.component.LibraryListItem
import abm.co.feature.book.model.BookUI
import abm.co.feature.utils.AnalyticsManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LibraryPage(
    navigateToBookDetailed: (BookUI) -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        AnalyticsManager.sendEvent(
            name = "library_page_viewed"
        )
    }
    viewModel.channel.collectInLaunchedEffect(function = { channel ->
        when (channel) {
            is LibraryContractChannel.NavigateToBookInfo -> {
                navigateToBookDetailed(channel.book)
            }

            is LibraryContractChannel.ShowMessage -> {
                showMessage(channel.messageContent)
            }
        }
    })
    val state by viewModel.state.collectAsState()

    SetStatusBarColor()
    Screen(
        uiState = state,
        onEvent = viewModel::onEvent
    )
}


@Composable
private fun Screen(
    uiState: LibraryContractState,
    onEvent: (LibraryContractEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .baseBackground()
            .fillMaxSize()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Toolbar()
        when (uiState) {
            LibraryContractState.Loading -> {
                LoadingScreen(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
            is LibraryContractState.Success -> {
                LazyColumn(
                    content = {
                        val grouped = uiState.books.groupBy { it.kind }
                        grouped.forEach { (kind, books) ->
                            item {
                                Column {
                                    LibraryKindTitle(
                                        title = kind,
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .fillMaxWidth()
                                    )
                                    Row(
                                        modifier = Modifier
                                            .horizontalScroll(rememberScrollState())
                                            .padding(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                                    ) {
                                        books.forEach { book ->
                                            LibraryListItem(
                                                book = book,
                                                onClick = {
                                                    onEvent(LibraryContractEvent.OnClickBook(book))
                                                }
                                            )
                                        }
                                    }
                                    Divider(
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 18.dp
                                        )
                                    )
                                }
                            }
                        }
                    }
                )
            }

            LibraryContractState.Empty -> {
                EmptyScreen()
            }
        }
    }
}

@Composable
private fun ColumnScope.EmptyScreen() {
    Spacer(modifier = Modifier.weight(0.27f))
    Image(
        modifier = Modifier
            .weight(0.3f)
            .aspectRatio(1f),
        contentScale = ContentScale.Fit,
        painter = painterResource(id = R.drawable.illustration_library),
        contentDescription = null
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = stringResource(id = R.string.Library_Info_Empty_title),
        style = StudyCardsTheme.typography.weight400Size16LineHeight24,
        color = StudyCardsTheme.colors.grayishBlue,
        modifier = Modifier.padding(horizontal = 16.dp),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.weight(0.28f))
}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        LoadingView()
    }
}

@Composable
private fun Toolbar(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.Library_Toolbar_title),
            style = StudyCardsTheme.typography.weight600Size14LineHeight18,
            color = StudyCardsTheme.colors.textPrimary,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
