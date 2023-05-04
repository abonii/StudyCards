package abm.co.designsystem.component.widget

import abm.co.designsystem.component.modifier.Modifier
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun LinearProgress(
    progressFloat: Float,
    modifier: Modifier = Modifier,
    contentColor: Color = Color(0xFF_AFCDFB),
    backgroundColor: Color = Color(0x5E_CADAE7),
    onReach100Percent: (() -> Unit)? = null
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(progressFloat){
        progress.animateTo(
            targetValue = progressFloat
        )
    }
    LaunchedEffect(progress.value) {
        if (progress.value >= 1) {
            onReach100Percent?.invoke()
        }
    }

    LinearProgressIndicator(
        modifier = modifier,
        progress = progress.value,
        color = contentColor,
        backgroundColor = backgroundColor
    )
}
