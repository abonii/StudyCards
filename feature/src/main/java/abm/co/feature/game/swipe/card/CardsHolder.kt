package abm.co.feature.game.swipe.card

import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.card.model.CardUI
import abm.co.feature.game.swipe.drag.DraggableCardController
import abm.co.feature.game.swipe.drag.draggableStack
import abm.co.feature.game.swipe.drag.moveTo
import abm.co.feature.game.swipe.drag.shadowPadding
import abm.co.feature.game.swipe.flip.Flippable
import abm.co.feature.game.swipe.flip.FlippableState
import abm.co.feature.game.swipe.flip.rememberFlipController
import abm.co.feature.game.swipe.shake.rememberShakeController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.ThresholdConfig
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * A stack of cards that can be dragged.
 *
 * @param items Cards to show in the stack.
 * @param thresholdConfig Specifies where the threshold between the predefined Anchors is. This is represented as a lambda
 * that takes two float and returns the threshold between them in the form of a [ThresholdConfig].
 *
 */
@ExperimentalMaterialApi
@Composable
fun CardsHolder(
    items: List<CardUI>,
    cardHeight: Dp,
    draggableCardController: DraggableCardController,
    frontContent: @Composable (item: CardUI, isFront: Boolean) -> Unit,
    backContent: @Composable (item: CardUI) -> Unit,
    modifier: Modifier = Modifier,
    thresholdConfig: (Float, Float) -> ThresholdConfig = { _, _ -> FractionalThreshold(0.2f) }
) {
    Box(
        modifier = modifier.height(cardHeight)
    ) {
        val defaultColor = StudyCardsTheme.colors.opposition
        if (items.lastIndex >= 1) {
            Box(
                modifier = Modifier
                    .shadowPadding(10.dp, draggableCardController, 2f)
                    .align(Alignment.Center)
                    .height(cardHeight)
                    .shadow(
                        elevation = 5.dp,
                        shape = RoundedCornerShape(12.dp),
                        ambientColor = Color.Transparent,
                        spotColor = defaultColor
                    ),
                content = {
                    frontContent(items[1], false)
                }
            )
        }
        if (items.lastIndex >= 0) {
            val scope = rememberCoroutineScope()
            val flipController = rememberFlipController()
            val shakeController = rememberShakeController()
            LaunchedEffect(Unit) {
                draggableCardController.preSwipe = {
                    flipController.reset()
                }
            }
            val frontModifier = Modifier
                .shadowPadding(10.dp, draggableCardController, 1f)
                .height(cardHeight)
                .draggableStack(
                    controller = draggableCardController,
                    thresholdConfig = thresholdConfig
                )
                .moveTo(
                    x = draggableCardController.offsetX.value,
                    y = draggableCardController.offsetY.value
                )
                .graphicsLayer(
                    rotationZ = draggableCardController.rotation.value,
                    translationX = shakeController.shakeOffset.value
                )
                .shadow(
                    shape = RoundedCornerShape(12.dp),
                    elevation = (maxOf(
                        abs(draggableCardController.offsetX.value),
                        abs(draggableCardController.offsetY.value)
                    ) / 15).coerceIn(1f, 30f).dp,
                    ambientColor = getShadowColor(draggableCardController, defaultColor),
                    spotColor = getShadowColor(draggableCardController, defaultColor)
                )
            Flippable(
                modifier = Modifier,
                frontSide = {
                    Box(
                        modifier = frontModifier,
                        content = { frontContent(items[0], true) }
                    )
                },
                backSide = {
                    Box(
                        modifier = frontModifier,
                        content = { backContent(items[0]) }
                    )
                },
                flipController = flipController,
                onClick = {
                    scope.launch {
                        if (flipController.currentSide == FlippableState.BACK) {
                            shakeController.doShake()
                        } else {
                            flipController.flip()
                        }
                    }
                }
            )
        }
    }
}


private fun getShadowColor(
    draggableCardController: DraggableCardController,
    defaultColor: Color
): Color {
    val horizontal = abs(draggableCardController.offsetX.value / draggableCardController.cardWidth)
    val vertical = abs(draggableCardController.offsetY.value / draggableCardController.cardHeight)
    val isVertical = vertical > horizontal
    return when {
        !isVertical && draggableCardController.offsetX.value > 0 -> {
            Color(0xFF_46A642)
        }

        !isVertical && draggableCardController.offsetX.value < 0 -> {
            Color(0xFF_FF453A)
        }

        isVertical && draggableCardController.offsetY.value > 0 -> {
            Color(0xFF_CFB323)
        }

        else -> defaultColor
    }
}
