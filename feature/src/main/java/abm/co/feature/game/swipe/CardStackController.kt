package abm.co.feature.game.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.material.SwipeableDefaults
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Controller of the [draggableStack] modifier.
 *
 * @param cardWidth The width of the screen used to calculate properties such as rotation and scale
 * @param animationSpec The default animation that will be used to animate swipes.
 *
 */
open class CardStackController(
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

    var onSwipe: (swipeSide: SwipeSide) -> Unit = {}

    fun swipeLeft() {
        scope.apply {
            launch {
                offsetX.animateTo(-cardWidth * 1.2f, tween(200))

                onSwipe(SwipeSide.START)

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

                onSwipe(SwipeSide.END)

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

                onSwipe(SwipeSide.TOP)

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

                onSwipe(SwipeSide.BOTTOM)

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
