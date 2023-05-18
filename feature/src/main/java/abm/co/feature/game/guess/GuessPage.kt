package abm.co.feature.game.guess

import abm.co.designsystem.component.dialog.ShowDialogOnBackPressed
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.toolbar.Toolbar
import abm.co.designsystem.extensions.collectInLifecycle
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.utils.AnalyticsManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun GuessPage(
    navigateBack: () -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: GuessViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        AnalyticsManager.sendEvent("guess_page_viewed")
    }
    val showDialog = remember { mutableStateOf(false) }
    viewModel.channel.collectInLifecycle {
        when (it) {
            is GuessContractChannel.ShowMessage -> showMessage(it.messageContent)
            GuessContractChannel.NavigateBack -> {
                showDialog.value = true
            }

            GuessContractChannel.Finished -> {
                navigateBack() // todo
            }
        }
    }
    ShowDialogOnBackPressed(
        show = showDialog,
        onConfirm = {
            navigateBack()
        }
    )
    val uiState = viewModel.state
    SetStatusBarColor()
    Screen(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Screen(
    uiState: GuessContractState,
    onEvent: (GuessContractEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .background(StudyCardsTheme.colors.backgroundPrimary)
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Toolbar(
            title = stringResource(id = R.string.Guess_Toolbar_title),
            onBack = {
                onEvent(GuessContractEvent.OnBack)
            }
        )
        uiState.item.value?.let { guessItemUI ->
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Question(
                    title = guessItemUI.question,
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxWidth()
                )
                LazyColumn(
                    modifier = Modifier.weight(0.6f),
                    userScrollEnabled = false
                ) {
                    items(
                        guessItemUI.answers,
                        key = { it.cardID }
                    ) { answer ->
                        AnswerItem(
                            title = answer.title,
                            modifier = Modifier
                                .animateItemPlacement()
                                .fillParentMaxHeight(1f / guessItemUI.answers.size)
                                .fillParentMaxWidth()
                                .padding(vertical = 10.dp),
                            onClick = {
                                onEvent(GuessContractEvent.OnSelectAnswer(answer))
                            },
                            questionID = guessItemUI.cardID,
                            answerID = answer.cardID
                        )
                    }
                }
            }
        }

    }
}

@Composable
private fun Question(
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(
                width = 0.5.dp,
                shape = RoundedCornerShape(16.dp),
                color = StudyCardsTheme.colors.primary
            )
            .clip(RoundedCornerShape(16.dp))
            .background(StudyCardsTheme.colors.backgroundPrimary)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(12.dp),
            text = title,
            style = StudyCardsTheme.typography.weight600Size16LineHeight18
        )
    }
}

@Composable
private fun AnswerItem(
    title: String,
    questionID: String,
    answerID: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCorrect = remember {
        mutableStateOf(false)
    }
    val isIncorrect = remember {
        mutableStateOf(false)
    }
    val updateClick by rememberUpdatedState(onClick)
    val animateBackground = animateColorAsState(
        targetValue = if (isCorrect.value) {
            StudyCardsTheme.colors.success
        } else if (isIncorrect.value) {
            StudyCardsTheme.colors.error
        } else StudyCardsTheme.colors.backgroundPrimary,
        label = "animateBackground",
        finishedListener = {
            isCorrect.value = false
            isIncorrect.value = false
            updateClick()
        }
    )
    Box(
        modifier = modifier
            .border(
                width = 0.5.dp,
                shape = RoundedCornerShape(16.dp),
                color = StudyCardsTheme.colors.primary
            )
            .clip(RoundedCornerShape(16.dp))
            .drawBehind {
                drawRect(color = animateBackground.value)
            }
            .clickable(onClick = {
                if (questionID == answerID) {
                    isCorrect.value = true
                } else {
                    isIncorrect.value = true
                }
            })
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(10.dp),
            text = title,
            style = StudyCardsTheme.typography.weight500Size14LineHeight20,
            color = StudyCardsTheme.colors.primary
        )
    }
}
