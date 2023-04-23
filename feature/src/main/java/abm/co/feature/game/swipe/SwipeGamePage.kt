package abm.co.feature.game.swipe

import abm.co.designsystem.component.measure.MeasureUnconstrainedViewHeight
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.widget.LoadingView
import abm.co.designsystem.extensions.collectInLaunchedEffect
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.card.model.CardUI
import abm.co.feature.game.swipe.card.BackCardItem
import abm.co.feature.game.swipe.card.FrontCardItem
import abm.co.feature.game.swipe.card.CardsHolder
import abm.co.feature.game.swipe.drag.rememberCardStackController
import abm.co.feature.utils.StudyCardsConstants.TOOLBAR_HEIGHT
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

@Composable
fun SwipeGamePage(
    onBack: () -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: SwipeGameViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        Firebase.analytics.logEvent(
            "swipe_game_page_viewed", null
        )
    }
    val state by viewModel.state.collectAsState()
    viewModel.channel.collectInLaunchedEffect {
        when(it){
            SwipeGameContractChannel.OnBack -> onBack()
            is SwipeGameContractChannel.ShowMessage -> showMessage(it.messageContent)
        }
    }
    SetStatusBarColor()
    GameScreen(
        screenState = state,
        event = viewModel::event
    )
}

@Composable
private fun GameScreen(
    screenState: SwipeGameContractState,
    event: (SwipeGameContractEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(StudyCardsTheme.colors.backgroundPrimary)
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Toolbar(
            title = screenState.categoryName,
            onBack = {
                event(SwipeGameContractEvent.OnBack)
            }
        )
        Crossfade(targetState = screenState) { state ->
            when (state) {
                is SwipeGameContractState.Loading -> {
                    LoadingScreen(modifier = Modifier.fillMaxSize())
                }
                is SwipeGameContractState.Success -> {
                    SuccessScreen(
                        modifier = Modifier.fillMaxSize(),
                        cards = state.cards,
                        event = event
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SuccessScreen(
    cards: List<CardUI>,
    event: (SwipeGameContractEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    MeasureUnconstrainedViewHeight(
        modifier = modifier,
        viewToMeasure = {
            KnownAndUnknownButtons(
                onClickRight = {},
                onClickLeft = {}
            )
        }, content = { height, _ ->
            BoxWithConstraints {
                val cardHeight = maxHeight - height
                val cardStackController = rememberCardStackController(cardHeight, maxWidth)
                cardStackController.onSwipe = {
                    event(SwipeGameContractEvent.OnSwipeOrClick(it))
                }
                KnownAndUnknownButtons(
                    onClickRight = {
                        cardStackController.swipeRight()
                    },
                    onClickLeft = {
                        cardStackController.swipeLeft()
                    }
                )
                CardsHolder(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(top = height),
                    items = cards,
                    cardHeight = cardHeight,
                    draggableCardController = cardStackController,
                    onSwipe = {
                        event(SwipeGameContractEvent.OnSwipeOrClick(it))
                    },
                    frontContent = { card, isFront ->
                        FrontCardItem(
                            cardUI = card,
                            isFront = isFront,
                            onClickUncertain = {
                                cardStackController.swipeBottom()
                            }
                        )
                    },
                    backContent = { card ->
                        BackCardItem(
                            cardUI = card,
                            onClickUncertain = {
                                cardStackController.swipeBottom()
                            }
                        )
                    }
                )
            }
        }
    )
}


@Composable
private fun KnownAndUnknownButtons(
    onClickRight: () -> Unit,
    onClickLeft: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 12.dp,
            )
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.clickableWithoutRipple(onClickLeft),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_left),
                tint = StudyCardsTheme.colors.textSecondary,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(9.dp))
            Text(
                text = stringResource(id = R.string.Game_Swipe_unknown),
                style = StudyCardsTheme.typography.weight400Size14LineHeight18,
                color = StudyCardsTheme.colors.textSecondary
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.clickableWithoutRipple(onClickRight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.Game_Swipe_know),
                style = StudyCardsTheme.typography.weight400Size14LineHeight18,
                color = StudyCardsTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.width(9.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                tint = StudyCardsTheme.colors.textSecondary,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun Toolbar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(TOOLBAR_HEIGHT)
            .fillMaxWidth()
            .padding(start = 6.dp, top = 6.dp, bottom = 6.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .clickableWithoutRipple(onBack)
                .padding(10.dp)
                .size(24.dp),
            painter = painterResource(id = R.drawable.ic_left),
            tint = StudyCardsTheme.colors.opposition,
            contentDescription = null
        )
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = StudyCardsTheme.typography.weight600Size14LineHeight18,
            color = StudyCardsTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = TOOLBAR_HEIGHT)
            .statusBarsPadding()
    ) {
        Spacer(modifier = Modifier.weight(0.5f))
        LoadingView(modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.weight(0.5f))
    }
}
