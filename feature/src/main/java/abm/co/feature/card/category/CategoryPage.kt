package abm.co.feature.card.category

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.widget.LinearProgress
import abm.co.designsystem.component.widget.LoadingView
import abm.co.designsystem.flow.collectInLaunchedEffect
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.card.category.component.CategoryCollapsingToolbar
import abm.co.feature.card.model.CardItemUI
import abm.co.feature.card.model.CardKindUI
import abm.co.feature.toolbar.ToolbarState
import abm.co.feature.toolbar.rememberToolbarState
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

private val MinToolbarHeight = 70.dp
private val MaxToolbarHeight = 150.dp

@Composable
fun CategoryPage(
    onBack: () -> Unit,
    navigateToEditCategory: () -> Unit,
    navigateToCard: (CardItemUI?) -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: CategoryViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        Firebase.analytics.logEvent(
            "category_page_viewed", null
        )
    }
    viewModel.channel.collectInLaunchedEffect {
        when (it) {
            CategoryContractChannel.NavigateBack -> onBack()
            is CategoryContractChannel.NavigateToCard -> navigateToCard(it.cardItem)
            CategoryContractChannel.NavigateToEditCategory -> navigateToEditCategory()
            is CategoryContractChannel.ShowMessage -> showMessage(it.messageContent)
        }
    }
    val screenState by viewModel.screenState.collectAsState()
    val toolbarState by viewModel.toolbarState.collectAsState()
    SetStatusBarColor()
    CategoryScreen(
        screenState = screenState,
        toolbarState = toolbarState,
        event = viewModel::event
    )
}

@Composable
private fun CategoryScreen(
    screenState: CategoryContract.ScreenState,
    toolbarState: CategoryContract.ToolbarState,
    event: (CategoryContractEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .background(StudyCardsTheme.colors.backgroundPrimary)
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        val toolbarScrollable = rememberToolbarState(
            minHeight = MinToolbarHeight,
            maxHeight = MaxToolbarHeight
        )
        val scrollState = rememberScrollState()
        ListenToScrollAndUpdateToolbarState(
            scrollState = scrollState,
            toolbarState = toolbarScrollable
        )
        Crossfade(targetState = screenState) { state ->
            when (state) {
                CategoryContract.ScreenState.Loading -> {
                    LoadingScreen(modifier = Modifier.fillMaxSize())
                }
                is CategoryContract.ScreenState.Empty -> {
                    EmptyScreen(modifier = Modifier.fillMaxSize())
                }
                is CategoryContract.ScreenState.Success -> {
                    SuccessScreen(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .fillMaxSize(),
                        screenState = state,
                        onClickCard = {
                            event(CategoryContractEvent.OnClickCardItem(it))
                        },
                        onClickPlay = {
                            event(CategoryContractEvent.OnClickPlayCard(it))
                        }
                    )
                }
            }
        }
        CategoryCollapsingToolbar(
            title = toolbarState.categoryName,
            subtitle = toolbarState.description,
            progress = toolbarScrollable.progress,
            onClickEndIcon = {
                event(CategoryContractEvent.OnClickEditCategory)
            },
            endIconRes = R.drawable.ic_add,
            modifier = Modifier
                .fillMaxWidth()
                .height(with(LocalDensity.current) { toolbarScrollable.height.toDp() })
                .graphicsLayer { translationY = toolbarScrollable.offset }
        )
    }
}


@Composable
private fun SuccessScreen(
    screenState: CategoryContract.ScreenState.Success,
    onClickCard: (CardItemUI) -> Unit,
    onClickPlay: (CardItemUI) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = MaxToolbarHeight)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        screenState.cards.forEach { card ->
            key(card.id) {
                CardItem(
                    cardItem = card,
                    onClick = { onClickCard(card) },
                    onClickPlay = { onClickPlay(card) },
                )
            }
        }
    }
}

@Composable
private fun CardItem(
    cardItem: CardItemUI,
    onClick: () -> Unit,
    onClickPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(70.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(StudyCardsTheme.colors.milky)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(11.dp))
                .background(
                    when (cardItem.kind) {
                        CardKindUI.UNDEFINED -> Color.Black
                        CardKindUI.UNKNOWN -> Color.Cyan
                        CardKindUI.UNCERTAIN -> Color.Blue
                        CardKindUI.KNOWN -> Color.Cyan
                    }
                )
                .width(6.dp)
                .fillMaxHeight()
        )
        Spacer(modifier = Modifier.width(24.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cardItem.name,
                color = StudyCardsTheme.colors.textPrimary,
                style = StudyCardsTheme.typography.weight500Size16LineHeight20
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = cardItem.translation,
                color = StudyCardsTheme.colors.textSecondary,
                style = StudyCardsTheme.typography.weight400Size12LineHeight20
            )
            Spacer(modifier = Modifier.width(8.dp))
            LinearProgress(
                progressFloat = cardItem.learnedPercent,
                contentColor = StudyCardsTheme.colors.blueMiddle,
                backgroundColor = StudyCardsTheme.colors.silver.copy(alpha = 0.15f),
                modifier = Modifier
                    .clip(RoundedCornerShape(7.dp))
                    .height(3.dp)
                    .fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            modifier = Modifier
                .clickableWithoutRipple(onClick = onClickPlay)
                .align(Alignment.CenterVertically)
                .padding(start = 10.dp, end = 12.dp, top = 10.dp, bottom = 10.dp)
                .size(24.dp),
            painter = painterResource(id = R.drawable.ic_play),
            tint = StudyCardsTheme.colors.buttonPrimary,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(12.dp))
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
private fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = MaxToolbarHeight)
            .statusBarsPadding()
    ) {
        Spacer(modifier = Modifier.weight(0.4f))
        LoadingView(modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.weight(0.6f))
    }
}

@Composable
private fun EmptyScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = MaxToolbarHeight)
            .statusBarsPadding()
    ) {
        Spacer(modifier = Modifier.weight(0.4f))
        Text(
            modifier = Modifier
                .padding(horizontal = 36.dp)
                .fillMaxWidth(),
            text = stringResource(R.string.HomePage_Empty_title),
            style = StudyCardsTheme.typography.weight400Size14LineHeight24,
            color = StudyCardsTheme.colors.middleGray,
            textAlign = TextAlign.Center
        )
        Spacer(
            modifier = Modifier
                .weight(0.6f)
                .align(Alignment.CenterHorizontally)
        )
    }
}
