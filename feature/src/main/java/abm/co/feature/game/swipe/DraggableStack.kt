package abm.co.feature.game.swipe

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.ThresholdConfig
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlin.math.abs
import kotlin.math.sign
import kotlinx.coroutines.launch

/**
 * Create and [remember] a [CardStackController] with the default animation clock.
 *
 * @param animationSpec The default animation that will be used to animate to a new state.
 */
@Composable
fun rememberCardStackController(
    cardHeight: Dp,
    cardWidth: Dp,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
): CardStackController {

    val scope = rememberCoroutineScope()

    val screenWidthPx = with(LocalDensity.current) { cardWidth.toPx() }
    val screenHeightPx = with(LocalDensity.current) { cardHeight.toPx() }

    return remember {
        CardStackController(
            scope = scope,
            cardWidth = screenWidthPx,
            cardHeight = screenHeightPx,
            animationSpec = animationSpec
        )
    }
}


/**
 * Enable drag gestures between a set of predefined anchors defined in [controller].
 *
 * @param controller The controller of the [draggableStack].
 * @param thresholdConfig Specifies where the threshold between the predefined Anchors is. This is represented as a lambda
 * that takes two float and returns the threshold between them in the form of a [ThresholdConfig].
 */
@OptIn(ExperimentalMaterialApi::class)
fun Modifier.draggableStack(
    controller: CardStackController,
    thresholdConfig: (Float, Float) -> ThresholdConfig,
): Modifier = composed {

    val density = LocalDensity.current
    val thresholds = { a: Float, b: Float ->
        with(thresholdConfig(a, b)) {
            density.computeThreshold(a, b)
        }
    }
    controller.threshold = thresholds(controller.center.x, controller.right.x)
    Modifier.pointerInput(Unit) {
        detectDragGestures(
            onDragEnd = {
                if (abs(controller.offsetX.value) <= abs(controller.offsetY.value)) {
//                    if (controller.offsetY.value < -controller.offsetToSwipeVertically) controller.swipeTop()
                    if (controller.offsetY.value > controller.offsetToSwipeVertically) controller.swipeBottom()
                    else controller.returnCenter()
                } else {
                    if (controller.offsetX.value < -controller.offsetToSwipeHorizontally) controller.swipeLeft()
                    else if (controller.offsetX.value > controller.offsetToSwipeHorizontally) controller.swipeRight()
                    else controller.returnCenter()
                }
            },
            onDrag = { change, dragAmount ->
                controller.scope.apply {
                    launch {
                        controller.offsetX.snapTo(
                            targetValue = controller.offsetX.value + dragAmount.x
                        )
                        val targetRotation = normalize(
                            controller.center.x,
                            controller.right.x,
                            abs(controller.offsetX.value),
                            0f,
                            24f
                        )
                        controller.rotation.snapTo(
                            targetValue = targetRotation * controller.offsetX.value.sign
                        )
                        controller.scale.snapTo(
                            normalize(
                                controller.center.x,
                                controller.right.x / 3,
                                abs(controller.offsetX.value),
                                0.9f
                            )
                        )
                    }
                    launch {
                        controller.offsetY.snapTo(
                            targetValue = controller.offsetY.value + dragAmount.y
                        )
                        controller.scale.snapTo(
                            normalize(
                                controller.center.y,
                                controller.bottom.y / 3,
                                abs(controller.offsetY.value),
                                0.9f
                            )
                        )
                    }
                }
                change.consume()
            }
        )
    }
}

fun normalize(
    min: Float,
    max: Float,
    v: Float,
    startRange: Float = 0f,
    endRange: Float = 1f
): Float {
    require(startRange < endRange) {
        "Start range is greater than End range"
    }
    val value = v.coerceIn(min, max)
    return (value - min) / (max - min) * (endRange - startRange) + startRange
}
