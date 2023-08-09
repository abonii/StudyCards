package abm.co.feature.card.category

import abm.co.designsystem.base.WrapperList
import abm.co.designsystem.base.toWrapperList
import abm.co.designsystem.component.dialog.ConfirmAlertDialog
import abm.co.designsystem.component.measure.MeasureUnconstrainedViewHeight
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.component.modifier.scalableClick
import abm.co.designsystem.component.statistics.CardsToLearn
import abm.co.designsystem.component.statistics.WeeklyReport
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.text.pluralString
import abm.co.designsystem.component.widget.LinearProgress
import abm.co.designsystem.component.widget.LoadingView
import abm.co.designsystem.extensions.collectInLaunchedEffect
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.card.model.CardKindUI
import abm.co.feature.card.model.CardUI
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.utils.AnalyticsManager
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

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
    val dialogState by viewModel.dialogState.collectAsState()
    SetStatusBarColor()
    CategoryScreen(
        screenState = screenState,
        toolbarState = toolbarState,
        dialogState = dialogState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun CategoryScreen(
    screenState: CategoryContract.ScreenState,
    toolbarState: CategoryContract.ToolbarState,
    dialogState: CategoryContract.Dialog,
    onEvent: (CategoryContractEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .background(StudyCardsTheme.colors.backgroundSecondary)
            .fillMaxSize()
    ) {
        when (screenState) {
            CategoryContract.ScreenState.Loading -> {
                Toolbar(
                    toolbarState = toolbarState,
                    onEvent = onEvent,
                    modifier = Modifier.fillMaxWidth()
                )
                LoadingScreen()
            }

            is CategoryContract.ScreenState.Empty -> {
                Toolbar(
                    toolbarState = toolbarState,
                    onEvent = onEvent,
                    modifier = Modifier.fillMaxWidth()
                )
                EmptyScreen()
            }

            is CategoryContract.ScreenState.Success -> {
                SuccessScreen(
                    modifier = Modifier.fillMaxSize(),
                    screenState = screenState,
                    toolbarState = toolbarState,
                    onEvent = onEvent
                )
            }
        }
        Dialog(
            dialogState = dialogState,
            onDismissDialog = {
                onEvent(CategoryContractEvent.OnDismissDialog)
            },
            onConfirmRemoveCard = {
                onEvent(CategoryContractEvent.OnConfirmRemoveCard(it))
            }
        )
    }
}

@Composable
private fun Dialog(
    dialogState: CategoryContract.Dialog,
    onConfirmRemoveCard: (CardUI) -> Unit,
    onDismissDialog: () -> Unit
) {
    dialogState.removingCard?.let {
        ConfirmAlertDialog(
            title = stringResource(id = R.string.Category_Dialog_removeCardItem, it.name),
            onConfirm = {
                onConfirmRemoveCard(it)
            },
            onDismiss = onDismissDialog
        )
    }
}

@Composable
private fun SuccessScreen(
    screenState: CategoryContract.ScreenState.Success,
    toolbarState: CategoryContract.ToolbarState,
    onEvent: (CategoryContractEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        item(contentType = "toolbar") {
            Toolbar(
                toolbarState = toolbarState,
                modifier = Modifier.fillParentMaxWidth(),
                onEvent = onEvent
            )
        }
        item(contentType = "statistics") {
            Statistics(
                modifier = Modifier.fillParentMaxWidth()
            )
        }
        items(
            items = screenState.cards,
            key = { it.cardID },
            contentType = { "cards" }
        ) { card ->
            CardItem(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp),
                cardItem = card,
                onClick = {
                    onEvent(CategoryContractEvent.Success.OnClickCardItem(card))
                },
                onClickPlay = {
                    onEvent(CategoryContractEvent.Success.OnClickPlayCard(card))
                },
                onLongClick = {
                    onEvent(CategoryContractEvent.Success.OnLongClickCard(card))
                }
            )
        }
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
        val scale = animateFloatAsState(if (pressed.value) 0.95f else 1f, label = "card scale")
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
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                }
                .height(70.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(StudyCardsTheme.colors.backgroundPrimary),
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

@Composable
private fun Toolbar(
    toolbarState: CategoryContract.ToolbarState,
    onEvent: (CategoryContractEvent.Toolbar) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(StudyCardsTheme.colors.backgroundPrimary)
            .padding(
                bottom = 12.dp,
                top = 32.dp
            )
    ) {
        Row {
            Icon(
                modifier = Modifier
                    .clickableWithoutRipple(onClick = {
                        onEvent(CategoryContractEvent.Toolbar.OnBack)
                    })
                    .padding(10.dp)
                    .size(24.dp),
                painter = painterResource(id = abm.co.designsystem.R.drawable.ic_left),
                tint = StudyCardsTheme.colors.opposition,
                contentDescription = null
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                modifier = Modifier
                    .clickableWithoutRipple(onClick = {
                        onEvent(CategoryContractEvent.Toolbar.OnClickNewCard)
                    })
                    .padding(10.dp)
                    .size(24.dp),
                painter = painterResource(id = R.drawable.ic_add_outlined),
                tint = StudyCardsTheme.colors.buttonPrimary,
                contentDescription = null
            )
            Icon(
                modifier = Modifier
                    .clickableWithoutRipple(onClick = {
                        onEvent(CategoryContractEvent.Toolbar.OnClickEditCategory)
                    })
                    .padding(10.dp)
                    .size(24.dp),
                painter = painterResource(id = R.drawable.ic_edit),
                tint = StudyCardsTheme.colors.buttonPrimary,
                contentDescription = null
            )
            Icon(
                modifier = Modifier
                    .clickableWithoutRipple(onClick = {
                        onEvent(CategoryContractEvent.Toolbar.OnClickShare)
                    })
                    .padding(10.dp)
                    .size(24.dp),
                painter = painterResource(id = R.drawable.ic_share),
                tint = StudyCardsTheme.colors.buttonPrimary,
                contentDescription = null
            )
        }
        AsyncImage(
            modifier = Modifier
                .height(150.dp)
                .background(Color(0xFF_B8BDC8)),
            model = toolbarState.categoryTitle,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            placeholder = painterResource(id = R.drawable.illustration_our_cards_empty),
            error = painterResource(id = R.drawable.illustration_our_cards_empty)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = toolbarState.categoryTitle,
            color = StudyCardsTheme.colors.primary,
            style = StudyCardsTheme.typography.weight600Size32LineHeight24,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = pluralString(
                id = R.plurals.cards,
                count = toolbarState.cardsCount
            ),
            color = StudyCardsTheme.colors.textPrimary,
            style = StudyCardsTheme.typography.weight500Size16LineHeight20,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = toolbarState.description,
            color = StudyCardsTheme.colors.textSecondary,
            style = StudyCardsTheme.typography.weight500Size16LineHeight20,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun Statistics(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp
        )
    ) {
        MeasureUnconstrainedViewHeight(
            viewToMeasure =
            {
                CardsToLearn(
                    modifier = Modifier,
                    cardsCount = 136, // todo
                    colorPoints = WrapperList(emptyList())
                )
            },
            content = { maxHeight, _ ->
                Row(horizontalArrangement = Arrangement.spacedBy(19.dp)) {
                    CardsToLearn(
                        modifier = Modifier
                            .height(maxHeight)
                            .weight(1f),
                        cardsCount = 136, // todo
                        colorPoints = listOf(
                            0.5f to StudyCardsTheme.colors.success,
                            0.3f to StudyCardsTheme.colors.uncertainStrong,
                            0.2f to StudyCardsTheme.colors.unknown,
                        ).toWrapperList()
                    )
                    WeeklyReport(
                        modifier = Modifier
                            .height(maxHeight)
                            .weight(1f),
                        weekDaysWithProgress = listOf(
                            "mo" to 0.4f,
                            "tu" to 0.2f,
                            "we" to 0.5f,
                            "th" to 0.6f,
                            "fr" to 0.3f,
                            "sn" to 1f,
                            "st" to 0.1f, // todo make it flexible for all languages
                        ).toWrapperList()
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            @Composable
            fun Item(title: String, color: Color) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Text(
                        text = title,
                        color = StudyCardsTheme.colors.textSecondary,
                        style = StudyCardsTheme.typography.weight400Size12LineHeight16,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Item(
                title = "Learned",
                color = StudyCardsTheme.colors.known
            )
            Item(
                title = "Uncertain",
                color = StudyCardsTheme.colors.uncertain
            )
            Item(
                title = "Unknown",
                color = StudyCardsTheme.colors.uncertain
            )
            Item(
                title = "Undefined",
                color = StudyCardsTheme.colors.blueMiddle
            )
        }
    }
}
