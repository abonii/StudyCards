package abm.co.designsystem.component.widget

import abm.co.designsystem.component.modifier.Modifier
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun LinearProgress(
    progressFloat: Float,
    modifier: Modifier = Modifier,
    contentColor: Color = Color(0x5E_CADAE7),
    backgroundColor: Color = Color.White,
    onReach100Percent: (() -> Unit)? = null
) {
    val progress by animateFloatAsState(
        targetValue = progressFloat,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        )
    )

    LaunchedEffect(progress) {
        if (progress >= 1) {
            onReach100Percent?.invoke()
        }
    }

    LinearProgressIndicator(
        modifier = modifier,
        progress = progress,
        color = backgroundColor,
        backgroundColor = contentColor
    )
}
