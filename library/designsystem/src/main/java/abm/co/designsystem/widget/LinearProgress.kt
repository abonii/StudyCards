package abm.co.designsystem.widget

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.theme.StudyCardsTheme
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LinearProgress(
    progressFloat: Float,
    onReach100Percent: () -> Unit,
    modifier: Modifier = Modifier
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
            onReach100Percent()
        }
    }

    LinearProgressIndicator(
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .height(6.dp)
            .fillMaxWidth(),
        progress = progress,
        color = Color.White,
        backgroundColor = Color(0x5E_CADAE7)
    )
}
