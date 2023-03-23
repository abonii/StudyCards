package abm.co.feature.game.swipe

import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.card.model.CardUI
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.ThresholdConfig
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * A stack of cards that can be dragged.
 *
 * @param items Cards to show in the stack.
 * @param shadowSide it for set side background items.
 *
 * @param thresholdConfig Specifies where the threshold between the predefined Anchors is. This is represented as a lambda
 * that takes two float and returns the threshold between them in the form of a [ThresholdConfig].
 *
 */
@ExperimentalMaterialApi
@Composable
fun SwipeCards(
    items: List<CardUI>,
    onSwipe: (SwipeSide) -> Unit,
    modifier: Modifier = Modifier,
    cardHeight: Dp,
    cardStackController: CardStackController,
    betweenMargin: Dp = 10.dp,
    shadowSide: CardShadowSide = CardShadowSide.ShadowBottom,
    thresholdConfig: (Float, Float) -> ThresholdConfig = { _, _ -> FractionalThreshold(0.2f) },
    content: @Composable ((item: CardUI) -> Unit)
) {
    if (items.isNotEmpty()) {
        Box(
            modifier = modifier.height(cardHeight)
        ) {
            if (items.lastIndex >= 2) {
                Box(
                    modifier = Modifier
                        .shadowHorizontalPadding(10.dp, cardStackController, 3f)
                        .align(Alignment.Center)
                        .height(cardHeight)
                        .shadow(
                            elevation = 5.dp,
                            shape = RoundedCornerShape(12.dp),
                            ambientColor = Color.Transparent,
                            spotColor = StudyCardsTheme.colors.opposition.copy(alpha = 0.5f)
                        )
                ) {
                    content(items[2])
                }
            }
            if (items.lastIndex >= 1) {
                Box(
                    modifier = Modifier
                        .shadowHorizontalPadding(10.dp, cardStackController, 2f)
                        .align(Alignment.Center)
                        .height(cardHeight)
                        .shadow(
                            elevation = 5.dp,
                            shape = RoundedCornerShape(12.dp),
                            ambientColor = Color.Transparent,
                            spotColor = StudyCardsTheme.colors.opposition.copy(alpha = 0.5f)
                        )
                ) { content(items[1]) }
            }
            if (items.lastIndex >= 0) {
                Box(
                    modifier = Modifier
                        .shadowHorizontalPadding(10.dp, cardStackController, 1f)
                        .align(Alignment.Center)
                        .height(cardHeight)
                        .draggableStack(
                            controller = cardStackController,
                            thresholdConfig = thresholdConfig
                        )
                        .moveTo(
                            x = cardStackController.offsetX.value,
                            y = cardStackController.offsetY.value
                        )
                        .graphicsLayer(
                            rotationZ = cardStackController.rotation.value,
                        )
                        .shadow(
                            shape = RoundedCornerShape(12.dp),
                            elevation = (maxOf(
                                abs(cardStackController.offsetX.value),
                                abs(cardStackController.offsetY.value)
                            ) / 20).coerceIn(1f, 30f).dp,
                            ambientColor = getShadowColor(cardStackController),
                            spotColor = getShadowColor(cardStackController)
                        )
                ) { content(items[0]) }
            }
        }
    }
}

@Composable
private fun getShadowColor(cardStackController: CardStackController): Color {
    val horizontal = abs(cardStackController.offsetX.value / cardStackController.cardWidth)
    val vertical = abs(cardStackController.offsetY.value / cardStackController.cardHeight)
    val isVertical = vertical > horizontal
    return when {
        !isVertical && cardStackController.offsetX.value > 0 -> {
            Color(0xFF_46A642)
        }
        !isVertical && cardStackController.offsetX.value < 0 -> {
            Color(0xFF_FF453A)
        }
        isVertical && cardStackController.offsetY.value > 0 -> {
            Color(0xFF_CFB323)
        }
        else -> StudyCardsTheme.colors.opposition.copy(alpha = 0.5f)
    }
}