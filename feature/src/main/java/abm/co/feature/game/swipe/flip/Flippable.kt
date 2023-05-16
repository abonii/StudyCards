package abm.co.feature.game.swipe.flip

import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.feature.game.swipe.flip.FlipAnimationType.HORIZONTAL_ANTI_CLOCKWISE
import abm.co.feature.game.swipe.flip.FlipAnimationType.HORIZONTAL_CLOCKWISE
import abm.co.feature.game.swipe.flip.FlipAnimationType.VERTICAL_ANTI_CLOCKWISE
import abm.co.feature.game.swipe.flip.FlipAnimationType.VERTICAL_CLOCKWISE
import abm.co.feature.game.swipe.flip.FlippableState.BACK
import abm.co.feature.game.swipe.flip.FlippableState.FRONT
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * An Enum class to keep the state of side of [Flippable] like [FRONT] or [BACK]
 */
enum class FlippableState {
    FRONT,
    BACK
}

/**
 * An Enum class for animation type of [Flippable]. It has these 4 states:
 * [HORIZONTAL_CLOCKWISE], [HORIZONTAL_ANTI_CLOCKWISE], [VERTICAL_CLOCKWISE], and [VERTICAL_ANTI_CLOCKWISE]
 */
enum class FlipAnimationType {
    /**
     * Rotates the [Flippable] horizontally in the clockwise direction
     */
    HORIZONTAL_CLOCKWISE,

    /**
     * Rotates the [Flippable] horizontally in the anti-clockwise direction
     */
    HORIZONTAL_ANTI_CLOCKWISE,

    /**
     * Rotates the [Flippable] vertically in the clockwise direction
     */
    VERTICAL_CLOCKWISE,

    /**
     * Rotates the [Flippable] vertically in the anti-clockwise direction
     */
    VERTICAL_ANTI_CLOCKWISE
}

/**
 *  A composable which creates a card-like flip view for [frontSide] and [backSide] composables.
 *
 *  @param frontSide [Composable] method to draw any view for the front side
 *  @param backSide [Composable] method to draw any view for the back side
 *  @param flipController A [FlippableController] which lets you control flipping programmatically.
 *  @param modifier The Modifier for this [Flippable]
 *  @param flipDurationMs The duration in Milliseconds for the flipping animation
 *  @param cameraDistance The [cameraDistance] for the flip animation. Sets the distance along the Z axis (orthogonal to the X/Y plane on which layers are drawn) from the camera to this layer.
 *
 */
@Composable
fun Flippable(
    frontSide: @Composable (BoxScope.() -> Unit),
    backSide: @Composable (BoxScope.() -> Unit),
    flipController: FlippableController,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    flipDurationMs: Int = 700,
    cameraDistance: Float = 30.0F
) {
    Box(
        modifier = modifier
            .clickableWithoutRipple(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        var flippableState = flipController.currentSide
        val transition: Transition<FlippableState> = updateTransition(
            targetState = flippableState,
            label = "Flip Transition",
        )

        LaunchedEffect(key1 = flipController, block = {
            flipController.flipRequests
                .onEach {
                    flippableState = it
                }
                .launchIn(this)
        })

        val frontRotation: Float by transition.animateFloat(
            transitionSpec = {
                when {
                    FRONT isTransitioningTo BACK -> {
                        keyframes {
                            durationMillis = flipDurationMs
                            0f at 0
                            180f at flipDurationMs
                        }
                    }

                    else -> snap()
                }
            },
            label = "Front Rotation"
        ) { state ->
            when (state) {
                FRONT -> 0f
                BACK -> 180f
            }
        }

        val backRotation: Float by transition.animateFloat(
            transitionSpec = {
                when {
                    FRONT isTransitioningTo BACK -> {
                        keyframes {
                            durationMillis = flipDurationMs
                            -180f at 0
                            0f at flipDurationMs
                        }
                    }

                    else -> snap()
                }
            },
            label = "Back Rotation"
        ) { state ->
            when (state) {
                FRONT -> 180f
                BACK -> 0f
            }
        }

        val frontOpacity: Float by transition.animateFloat(
            transitionSpec = {
                when {
                    FRONT isTransitioningTo BACK -> {
                        keyframes {
                            durationMillis = flipDurationMs
                            1f at 0
                            1f at (flipDurationMs / 2) - 1
                            0f at flipDurationMs / 2
                            0f at flipDurationMs
                        }
                    }

                    else -> snap()
                }
            },
            label = "Front Opacity"
        ) { state ->
            when (state) {
                FRONT -> 1f
                BACK -> 0f
            }
        }

        val backOpacity: Float by transition.animateFloat(
            transitionSpec = {
                when {
                    FRONT isTransitioningTo BACK -> {
                        keyframes {
                            durationMillis = flipDurationMs
                            0f at 0
                            0f at (flipDurationMs / 2) - 1
                            1f at flipDurationMs / 2
                            1f at flipDurationMs
                        }
                    }

                    else -> snap()
                }
            },
            label = "Back Opacity"
        ) { state ->
            when (state) {
                FRONT -> 0f
                BACK -> 1f
            }
        }
        Box(modifier = Modifier
            .graphicsLayer {
                this.cameraDistance = cameraDistance
                this.rotationY = backRotation
            }
            .alpha(backOpacity)
            .zIndex(backOpacity),
            content = backSide
        )

        Box(modifier = Modifier
            .graphicsLayer {
                this.cameraDistance = cameraDistance
                this.rotationY = frontRotation
            }
            .alpha(frontOpacity)
            .zIndex(frontOpacity),
            content = frontSide
        )
    }
}
