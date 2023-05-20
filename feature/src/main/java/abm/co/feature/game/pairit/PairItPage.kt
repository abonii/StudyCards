package abm.co.feature.game.pairit

import abm.co.designsystem.component.dialog.ShowDialogOnBackPressed
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.toolbar.Toolbar
import abm.co.designsystem.extensions.collectInLifecycle
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.game.pairit.model.PairItemUI
import abm.co.feature.game.swipe.shake.rememberShakeController
import abm.co.feature.utils.AnalyticsManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

@Composable
fun PairItPage(
    nextPageAfterFinish: () -> Unit,
    navigateBack: (isRepeat: Boolean) -> Unit,
    onProgressChanged: (Float) -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: PairItViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        AnalyticsManager.sendEvent("pair_it_page_viewed")
    }
    val uiState = viewModel.uiState
    viewModel.channel.collectInLifecycle {
        when (it) {
            is PairItContractChannel.ShowMessage -> {
                showMessage(it.messageContent)
            }

            PairItContractChannel.Finished -> {
                if (uiState.isRepeat) {
                    nextPageAfterFinish()
                } else {
                    navigateBack(false)
                }
            }
        }
    }
    viewModel.progress.collectInLifecycle {
        onProgressChanged(it)
    }
    val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()
    ShowDialogOnBackPressed(
        show = dialogState.backPressConfirm,
        onConfirm = {
            navigateBack(uiState.isRepeat)
        },
        onDismiss = {
            viewModel.onEvent(PairItContractEvent.OnDismissDialog)
        }
    )
    SetStatusBarColor()
    Screen(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun Screen(
    uiState: PairItContractState,
    onEvent: (PairItContractEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .background(StudyCardsTheme.colors.backgroundPrimary)
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        if (!uiState.isRepeat) {
            Toolbar(
                modifier = Modifier.statusBarsPadding(),
                title = stringResource(id = R.string.PairIt_Toolbar_title),
                onBack = {
                    onEvent(PairItContractEvent.OnBack)
                }
            )
        }
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SingleColumn(
                items = uiState.nativeItems,
                correctItemsID = uiState.correctNativeItemsID,
                incorrectItemID = uiState.incorrectNativeItemID,
                selectedItemID = uiState.selectedNativeItemID,
                onClickItem = {
                    onEvent(PairItContractEvent.OnClickNativeCard(it))
                },
                onIncorrectAnimationFinished = {
                    onEvent(PairItContractEvent.OnNativeIncorrectAnimationFinished(it))
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 5.dp)
            )
            SingleColumn(
                items = uiState.learningItems,
                correctItemsID = uiState.correctLearningItemsID,
                incorrectItemID = uiState.incorrectLearningItemID,
                selectedItemID = uiState.selectedLearningItemID,
                onClickItem = {
                    onEvent(PairItContractEvent.OnClickLearningCard(it))
                },
                onIncorrectAnimationFinished = {
                    onEvent(PairItContractEvent.OnLearningIncorrectAnimationFinished(it))
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 5.dp)
            )
        }
    }
}

@Composable
private fun SingleColumn(
    items: SnapshotStateList<PairItemUI>,
    correctItemsID: List<String>,
    incorrectItemID: State<String?>,
    selectedItemID: State<String?>,
    onClickItem: (PairItemUI) -> Unit,
    onIncorrectAnimationFinished: (PairItemUI) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items.forEach { item ->
            key(item.cardID) {
                Item(
                    item = item,
                    isCorrect = remember {
                        derivedStateOf {
                            correctItemsID.contains(item.cardID)
                        }
                    }.value,
                    isSelected = selectedItemID.value == item.cardID,
                    isIncorrect = incorrectItemID.value == item.cardID,
                    onClick = {
                        onClickItem(item)
                    },
                    onIncorrectAnimationFinished = {
                        onIncorrectAnimationFinished(item)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

            }
        }
        repeat(5 - items.size) {
            Spacer(
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun Item(
    item: PairItemUI,
    isCorrect: Boolean,
    isSelected: Boolean,
    isIncorrect: Boolean,
    onClick: () -> Unit,
    onIncorrectAnimationFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isCorrect) 0f else 1f,
        label = "scale item",
        animationSpec = tween(durationMillis = SCALE_DURATION, easing = LinearEasing)
    )
    val coroutineScope = rememberCoroutineScope()
    val shakeController = rememberShakeController()
    val backgroundColor by animateColorAsState(
        if (isCorrect) {
            StudyCardsTheme.colors.success
        } else if (isIncorrect) {
            StudyCardsTheme.colors.error
        } else {
            StudyCardsTheme.colors.backgroundPrimary
        },
        label = "backgroundColor",
        finishedListener = {
            coroutineScope.launch {
                if (isIncorrect) {
                    shakeController.doShake()
                    onIncorrectAnimationFinished()
                }
            }
        }
    )

    Box(
        modifier = modifier
            .graphicsLayer(
                translationX = shakeController.shakeOffset.value,
                scaleX = scale,
                scaleY = scale
            )
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Transparent,
                spotColor = StudyCardsTheme.colors.primary
            )
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = if (isSelected) StudyCardsTheme.colors.primary else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .drawBehind {
                drawRect(color = backgroundColor)
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .padding(12.dp),
            text = item.title,
            style = StudyCardsTheme.typography.weight500Size14LineHeight20
        )
    }
}
