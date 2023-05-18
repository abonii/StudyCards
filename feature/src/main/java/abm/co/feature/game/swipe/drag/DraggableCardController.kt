package abm.co.feature.game.swipe.drag

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.material.SwipeableDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * sides that can be swiped
 */
sealed interface DraggableSide {
    object START : DraggableSide
    object TOP : DraggableSide
    object END : DraggableSide
    object BOTTOM : DraggableSide
}

/**
 * Create and [remember] a [DraggableCardController] with the default animation clock.
 *
 * @param animationSpec The default animation that will be used to animate to a new state.
 */
@Composable
fun rememberCardStackController(
    cardHeight: Dp,
    cardWidth: Dp,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
): DraggableCardController {

    val scope = rememberCoroutineScope()

    val screenWidthPx = with(LocalDensity.current) { cardWidth.toPx() }
    val screenHeightPx = with(LocalDensity.current) { cardHeight.toPx() }

    return remember {
        DraggableCardController(
            scope = scope,
            cardWidth = screenWidthPx,
            cardHeight = screenHeightPx,
            animationSpec = animationSpec
        )
    }
}

/**
 * Controller of the [draggableStack] modifier.
 *
 * @param cardWidth The width of the screen used to calculate properties such as rotation and scale
 * @param animationSpec The default animation that will be used to animate swipes.
 *
 */
@Immutable
open class DraggableCardController(
    val scope: CoroutineScope,
    val cardWidth: Float,
    val cardHeight: Float,
    internal val animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec
) {

    /**
     * offsetToSwipe
     */
    val offsetToSwipeHorizontally = cardWidth / 4
    val offsetToSwipeVertically = cardHeight / 4.5

    /**
     * Anchors
     */
    val right = Offset(cardWidth, 0f)
    val bottom = Offset(0f, cardHeight)
    val center = Offset(0f, 0f)

    /**
     * Threshold to start swiping
     */
    var threshold: Float = 0.0f

    /**
     * The current position (in pixels) of the First Card.
     */
    val offsetX = Animatable(0f)
    val offsetY = Animatable(0f)

    /**
     * The current rotation (in pixels) of the First Card.
     */
    val rotation = Animatable(0f)

    /**
     * The current scale factor (in pixels) of the Card before the first one displayed.
     */
    val scale = Animatable(0.9f)

    var preSwipe: suspend (draggableSide: DraggableSide) -> Unit = {}
    var onSwipe: (draggableSide: DraggableSide) -> Unit = {}

    fun swipeLeft() {
        scope.apply {
            launch {
                offsetX.animateTo(-cardWidth * 1.2f, tween(200))
                preSwipe(DraggableSide.START)
                delay(50)
                onSwipe(DraggableSide.START)

                // After the animation of swiping return back to Center to make it look like a cycle
                launch {
                    offsetX.snapTo(center.x)
                }
                launch {
                    offsetY.snapTo(0f)
                }
                launch {
                    rotation.snapTo(0f)
                }
                launch {
                    scale.snapTo(0.9f)
                }
            }

            launch {
                scale.animateTo(1f, tween(200))
            }
        }

    }

    fun swipeRight() {
        scope.apply {
            launch {
                offsetX.animateTo(cardWidth * 1.2f, tween(200))

                preSwipe(DraggableSide.END)
                delay(50)
                onSwipe(DraggableSide.END)

                // After the animation return back to Center to make it look like a cycle
                launch {
                    offsetX.snapTo(center.x)
                }
                launch {
                    offsetY.snapTo(0f)
                }
                launch {
                    scale.snapTo(0.9f)
                }
                launch {
                    rotation.snapTo(0f)
                }
            }

            launch {
                scale.animateTo(1f, tween(200))
            }
        }

    }

    fun swipeTop() {
        scope.apply {
            launch {
                offsetY.animateTo(-cardHeight * 1.2f, tween(200))

                preSwipe(DraggableSide.TOP)
                delay(50)
                onSwipe(DraggableSide.TOP)

                // After the animation return back to Center to make it look like a cycle
                launch {
                    offsetX.snapTo(center.x)
                }
                launch {
                    offsetY.snapTo(0f)
                }
                launch {
                    scale.snapTo(0.9f)
                }
                launch {
                    rotation.snapTo(0f)
                }
            }

            launch {
                scale.animateTo(1.5f, tween(200))
            }
        }

    }

    fun swipeBottom() {
        scope.apply {
            launch {
                offsetY.animateTo(cardHeight * 1.2f, tween(200))

                preSwipe(DraggableSide.BOTTOM)
                delay(50)
                onSwipe(DraggableSide.BOTTOM)

                // After the animation return back to Center to make it look like a cycle
                launch {
                    offsetX.snapTo(center.x)
                }
                launch {
                    offsetY.snapTo(0f)
                }
                launch {
                    scale.snapTo(0.9f)
                }
                launch {
                    rotation.snapTo(0f)
                }
            }

            launch {
                scale.animateTo(1f, tween(200))
            }
        }

    }

    fun returnCenter() {
        scope.apply {
            launch {
                offsetX.animateTo(center.x, tween(200))
            }
            launch {
                offsetY.animateTo(center.y, tween(200))
            }
            launch {
                rotation.animateTo(0f, tween(200))
            }
            launch {
                scale.animateTo(0.9f, tween(200))
            }
        }
    }

}
