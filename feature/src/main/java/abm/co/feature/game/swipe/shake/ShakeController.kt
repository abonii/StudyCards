package abm.co.feature.game.swipe.shake

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

class ShakeController(
    private val shakeOneTime: Boolean = false
) {
    private var shakeEnabled: Boolean = true
    internal val shakeOffset = mutableStateOf(0f)

    suspend fun doShake() {
        if (shakeEnabled) {
            shakeEnabled = !shakeOneTime
            animate(
                initialValue = -15f,
                targetValue = 15f,
                animationSpec = repeatable(
                    iterations = 3,
                    animation = tween(
                        durationMillis = 70,
                        easing = LinearEasing,
                        delayMillis = 20
                    )
                ),
                block = { value, _ ->
                    shakeOffset.value = value
                }
            )
            reset()
        }
    }

    private fun reset() {
        shakeOffset.value = 0f
    }
}

/**
 * Creates an instance of [ShakeController] and remembers it for recomposition.
 */
@Composable
fun rememberShakeController(
    shakeOneTime: Boolean = false
): ShakeController {
    return remember { ShakeController(shakeOneTime = shakeOneTime) }
}