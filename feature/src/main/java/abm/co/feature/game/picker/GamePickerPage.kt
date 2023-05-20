package abm.co.feature.game.picker

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.scalableClick
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.text.pluralString
import abm.co.designsystem.extensions.collectInLaunchedEffect
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.card.model.CardUI
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.game.model.GameKindUI
import abm.co.feature.utils.AnalyticsManager
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun GamePickerPage(
    navigateToGame: (GameKindUI, List<CardUI>) -> Unit,
    navigateToLearn: (CategoryUI) -> Unit,
    navigateToRepeat: (List<CardUI>) -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: GamePickerViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        AnalyticsManager.sendEvent(
            name = "game_picker_page_viewed"
        )
    }
    val state by viewModel.state.collectAsState()
    viewModel.channel.collectInLaunchedEffect { channel ->
        when (channel) {
            is GamePickerContractChannel.NavigateToGame -> {
                navigateToGame(channel.gameKind, channel.cards)
            }

            is GamePickerContractChannel.NavigateToLearn -> {
                navigateToLearn(channel.category)
            }

            is GamePickerContractChannel.NavigateToRepeat -> {
                navigateToRepeat(channel.cards)
            }

            is GamePickerContractChannel.ShowMessage -> showMessage(channel.messageContent)
        }
    }

    SetStatusBarColor()
    GamePickerScreen(
        uiState = state,
        onEvent = viewModel::onEvent
    )
}

@Stable
@Composable
private fun GamePickerScreen(
    uiState: GamePickerContractState,
    onEvent: (GamePickerContractEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .background(
                color = StudyCardsTheme.colors.backgroundPrimary,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Item(
            title = stringResource(id = R.string.GamePicker_Learn_title),
            iconRes = R.drawable.ic_play,
            subtitle = stringResource(
                id = R.string.GamePicker_Learn_subtitle,
                pluralString(id = R.plurals.new_cards, uiState.cardsToLearn)
            ),
            onClick = {
                onEvent(
                    GamePickerContractEvent.OnLearnPicked
                )
            },
            iconColor = StudyCardsTheme.colors.buttonPrimary
        )
        Divider(
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Item(
            title = stringResource(id = R.string.GamePicker_Repeat_title),
            iconRes = R.drawable.ic_repeat,
            subtitle = if (uiState.cardsToRepeat <= 0 && uiState.leftHours != null) {
                pluralString(
                    id = uiState.leftHours.first,
                    count = uiState.leftHours.second
                )
            } else {
                stringResource(
                    id = R.string.GamePicker_Repeat_subtitle,
                    pluralString(id = R.plurals.cards, uiState.cardsToRepeat)
                )
            },
            onClick = {
                onEvent(
                    GamePickerContractEvent.OnRepeatPicked
                )
            }
        )
        Divider(
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Item(
            title = stringResource(id = R.string.GamePicker_OneGame_title),
            iconRes = R.drawable.ic_one_game,
            subtitle = stringResource(
                id = R.string.GamePicker_OneGame_subtitle,
                pluralString(id = R.plurals.new_cards, uiState.cardsToLearn),
                pluralString(id = R.plurals.cards, uiState.actuallyCardsToRepeat)
            ),
            onClick = {
                onEvent(
                    GamePickerContractEvent.OnOneGameToggled
                )
            }
        )
        Column(
            modifier = Modifier.animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (uiState.oneGameExpanded) {
                Item(
                    title = stringResource(id = R.string.GamePicker_Pairing_title),
                    iconRes = R.drawable.ic_pairing,
                    subtitle = null,
                    onClick = {
                        onEvent(
                            GamePickerContractEvent.OnGamePicked(GameKindUI.Pair)
                        )
                    }
                )
                Item(
                    title = stringResource(id = R.string.GamePicker_Guessing_title),
                    iconRes = R.drawable.ic_guessing,
                    subtitle = null,
                    onClick = {
                        onEvent(
                            GamePickerContractEvent.OnGamePicked(GameKindUI.Guess)
                        )
                    }
                )
            }
        }
        Divider(
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Item(
            title = stringResource(id = R.string.GamePicker_Review_title),
            iconRes = R.drawable.ic_review,
            subtitle = stringResource(
                id = R.string.GamePicker_Review_subtitle,
                pluralString(id = R.plurals.cards, uiState.allCards)
            ),
            onClick = {
                onEvent(
                    GamePickerContractEvent.OnGamePicked(GameKindUI.Review)
                )
            }
        )
    }
}

@Composable
private fun Item(
    title: String,
    subtitle: String?,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconColor: Color = StudyCardsTheme.colors.grayishWhite
) {
    Row(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 14.dp)
            .scalableClick(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = iconColor
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = StudyCardsTheme.typography.weight600Size14LineHeight18,
                color = StudyCardsTheme.colors.grayishBlack,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = StudyCardsTheme.typography.weight400Size14LineHeight20,
                    color = StudyCardsTheme.colors.grayishWhite,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
