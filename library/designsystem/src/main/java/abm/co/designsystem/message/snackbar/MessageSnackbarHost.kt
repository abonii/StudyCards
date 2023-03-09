package abm.co.designsystem.message.snackbar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material.SnackbarData
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.RecomposeScope
import androidx.compose.runtime.State
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.AccessibilityManager
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.delay

@Composable
fun MessageSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    snackbar: @Composable (SnackbarData) -> Unit
) {
    val currentSnackbarData = hostState.currentSnackbarData
    val accessibilityManager = LocalAccessibilityManager.current
    LaunchedEffect(currentSnackbarData) {
        if (currentSnackbarData != null) {
            val duration = currentSnackbarData.duration.toMillis(
                currentSnackbarData.actionLabel != null,
                accessibilityManager
            )
            delay(duration)
            currentSnackbarData.dismiss()
        }
    }
    UpToDownAnimation(
        current = hostState.currentSnackbarData,
        modifier = modifier,
        content = snackbar
    )
}

@Composable
private fun UpToDownAnimation(
    current: SnackbarData?,
    modifier: Modifier = Modifier,
    content: @Composable (SnackbarData) -> Unit
) {
    val state = remember { UpToDownAnimationState<SnackbarData?>() }
    if (current != state.current) {
        state.current = current
        val keys = state.items.map { it.key }.toMutableList()
        if (!keys.contains(current)) {
            keys.add(current)
        }
        state.items.clear()
        keys.filterNotNull().mapTo(state.items) { key ->
            UpToDownAnimationItem(key) { children ->
                val isVisible = key == current
                val duration = if (isVisible) SnackbarUpToDownMillis else SnackbarDownToUpMillis
                val delay = SnackbarDownToUpMillis
                val animationDelay = if (isVisible && keys.filterNotNull().size != 1) delay else 0
                val offsetTop = animatedValue(
                    animation = tween(
                        easing = LinearEasing,
                        delayMillis = animationDelay,
                        durationMillis = duration
                    ),
                    visible = isVisible,
                    valueForVisible = 0F,
                    valueForNotVisible = -1000F
                )
                Box(
                    Modifier
                        .graphicsLayer(
                            translationY = offsetTop.value
                        )
                        .semantics {
                            liveRegion = LiveRegionMode.Polite
                            dismiss { key.dismiss(); true }
                        }
                ) {
                    children()
                }
            }
        }
    }
    Box(modifier) {
        state.scope = currentRecomposeScope
        state.items.forEach { (item, transition) ->
            key(item) {
                transition {
                    content(item ?: return@transition)
                }
            }
        }
    }
}

private fun SnackbarDuration.toMillis(
    hasAction: Boolean,
    accessibilityManager: AccessibilityManager?
): Long {
    val original = when (this) {
        SnackbarDuration.Indefinite -> Long.MAX_VALUE
        SnackbarDuration.Long -> 10000L
        SnackbarDuration.Short -> 4000L
    }
    if (accessibilityManager == null) {
        return original
    }
    return accessibilityManager.calculateRecommendedTimeoutMillis(
        original,
        containsIcons = true,
        containsText = true,
        containsControls = hasAction
    )
}

@Composable
private fun animatedValue(
    animation: AnimationSpec<Float>,
    visible: Boolean,
    valueForVisible: Float,
    valueForNotVisible: Float
): State<Float> {
    val alpha = remember { Animatable(if (!visible) valueForVisible else valueForNotVisible) }
    LaunchedEffect(visible) {
        alpha.animateTo(
            if (visible) valueForVisible else valueForNotVisible,
            animationSpec = animation
        )
        delay(3000)
        if (!visible) {
            return@LaunchedEffect
        }
        alpha.animateTo(
            if (visible) valueForNotVisible else valueForVisible,
            animationSpec = animation
        )
    }
    return alpha.asState()
}

private class UpToDownAnimationState<T> {
    // we use Any here as something which will not be equals to the real initial value
    var current: Any? = Any()
    var items = mutableListOf<UpToDownAnimationItem<T>>()
    var scope: RecomposeScope? = null
}

private data class UpToDownAnimationItem<T>(
    val key: T,
    val transition: UpToDownTransition
)

private typealias UpToDownTransition = @Composable (content: @Composable () -> Unit) -> Unit

private const val SnackbarUpToDownMillis = 300
private const val SnackbarDownToUpMillis = 150
