package abm.co.feature.card.category

import abm.co.designsystem.component.dialog.ConfirmAlertDialog
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.component.modifier.scalableClick
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.widget.LinearProgress
import abm.co.designsystem.component.widget.LoadingView
import abm.co.designsystem.extensions.collectInLaunchedEffect
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.card.category.component.CategoryCollapsingToolbar
import abm.co.feature.card.model.CardKindUI
import abm.co.feature.card.model.CardUI
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.toolbar.ToolbarState
import abm.co.feature.toolbar.rememberToolbarState
import abm.co.feature.utils.AnalyticsManager
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

private val MinToolbarHeight = 80.dp
private val MaxToolbarHeight = 120.dp

@Composable
fun CategoryPage(
    onBack: () -> Unit,
    navigateToCard: (CardUI?, CategoryUI) -> Unit,
    navigateToChangeCategory: (CategoryUI) -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: CategoryViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        AnalyticsManager.sendEvent("category_page_viewed")
    }
    viewModel.channel.collectInLaunchedEffect {
        when (it) {
            CategoryContractChannel.NavigateBack -> onBack()
            is CategoryContractChannel.NavigateToCard -> navigateToCard(it.cardItem, it.category)
            is CategoryContractChannel.ShowMessage -> showMessage(it.messageContent)
            is CategoryContractChannel.NavigateToChangeCategory -> navigateToChangeCategory(it.category)
        }
    }
    val screenState by viewModel.state.collectAsState()
    val toolbarState by viewModel.toolbarState.collectAsState()
    SetStatusBarColor()
    CategoryScreen(
        screenState = screenState,
        toolbarState = toolbarState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun CategoryScreen(
    screenState: CategoryContract.ScreenState,
    toolbarState: CategoryContract.ToolbarState,
    onEvent: (CategoryContractEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .background(StudyCardsTheme.colors.backgroundPrimary)
            .fillMaxSize()
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
        Crossfade(
            targetState = screenState,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) { state ->
            when (state) {
                CategoryContract.ScreenState.Loading -> {
                    LoadingScreen()
                }
                is CategoryContract.ScreenState.Empty -> {
                    EmptyScreen()
                }
                is CategoryContract.ScreenState.Success -> {
                    SuccessScreen(
                        modifier = Modifier.fillMaxSize(),
                        screenState = state,
                        onClickCard = {
                            onEvent(CategoryContractEvent.OnClickCardItem(it))
                        },
                        onClickPlay = {
                            onEvent(CategoryContractEvent.OnClickPlayCard(it))
                        },
                        onLongClick = {
                            onEvent(CategoryContractEvent.OnLongClickCard(it))
                        },
                        onDismissDialog = {
                            onEvent(CategoryContractEvent.OnDismissDialog)
                        },
                        onConfirmRemoveCard = {
                            onEvent(CategoryContractEvent.OnConfirmRemoveCard(it))
                        }
                    )
                }
            }
        }
        CategoryCollapsingToolbar(
            title = toolbarState.categoryTitle,
            subtitle = stringResource(id = toolbarState.descriptionRes),
            progress = toolbarScrollable.progress,
            onClickAddCardIcon = {
                onEvent(CategoryContractEvent.OnClickNewCard)
            },
            onClickChangeCategoryIcon = {
                onEvent(CategoryContractEvent.OnClickEditCategory)
            },
            addCardIconRes = R.drawable.ic_add,
            changeCategoryIconRes = R.drawable.ic_edit,
            modifier = Modifier
                .fillMaxWidth()
                .height(with(LocalDensity.current) { toolbarScrollable.height.toDp() })
                .graphicsLayer { translationY = toolbarScrollable.offset },
            onBack = {
                onEvent(CategoryContractEvent.OnBackClicked)
            },
            onChangeTitle = {
                onEvent(CategoryContractEvent.OnClickEditCategory)
            }
        )
    }
}


@Composable
private fun SuccessScreen(
    screenState: CategoryContract.ScreenState.Success,
    onClickCard: (CardUI) -> Unit,
    onClickPlay: (CardUI) -> Unit,
    onLongClick: (CardUI) -> Unit,
    onConfirmRemoveCard: (CardUI) -> Unit,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = MaxToolbarHeight)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        screenState.cards.forEach { card ->
            key(card.cardID) {
                CardItem(
                    cardItem = card,
                    onClick = { onClickCard(card) },
                    onClickPlay = { onClickPlay(card) },
                    onLongClick = { onLongClick(card) }
                )
            }
        }
    }
    screenState.removingCard?.let {
        ConfirmAlertDialog(
            title = "Do you really want to delete ${it.name}?",
            onConfirm = {
                onConfirmRemoveCard(it)
            },
            onDismiss = onDismissDialog
        )
    }
}

@Composable
private fun CardItem(
    cardItem: CardUI,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onClickPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box {
        val pressed = rememberSaveable { mutableStateOf(false) }
        val scale = animateFloatAsState(if (pressed.value) 0.95f else 1f)
        Spacer(
            modifier = Modifier
                .scalableClick(
                    pressed = pressed,
                    onClick = { onClick.invoke() },
                    onLongClick = { onLongClick.invoke() }
                )
                .matchParentSize()
        )
        Row(
            modifier = modifier
                .scale(scale.value)
                .height(70.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(StudyCardsTheme.colors.milky),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(11.dp))
                    .background(
                        when (cardItem.kind) {
                            CardKindUI.UNDEFINED -> StudyCardsTheme.colors.blueMiddle
                            CardKindUI.UNKNOWN -> StudyCardsTheme.colors.unknown
                            CardKindUI.UNCERTAIN -> StudyCardsTheme.colors.uncertain
                            CardKindUI.KNOWN -> StudyCardsTheme.colors.known
                        }
                    )
                    .width(6.dp)
                    .fillMaxHeight()
            )
            Spacer(modifier = Modifier.width(24.dp))
            Column(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = cardItem.name,
                    color = StudyCardsTheme.colors.textPrimary,
                    style = StudyCardsTheme.typography.weight500Size16LineHeight20,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = cardItem.translation,
                    color = StudyCardsTheme.colors.textSecondary,
                    style = StudyCardsTheme.typography.weight400Size12LineHeight20,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
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
            .fillMaxWidth()
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
        modifier = modifier.padding(top = MaxToolbarHeight)
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
