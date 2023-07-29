package abm.co.designsystem.component.statistics

import abm.co.designsystem.base.WrapperList
import abm.co.designsystem.base.toWrapperList
import abm.co.designsystem.theme.StudyCardsTheme
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimatableProgressView(
    colorPoints: WrapperList<Pair<Float, Color>>,
    modifier: Modifier = Modifier,
    height: Dp = 9.dp,
    backgroundColor: Color = StudyCardsTheme.colors.backgroundSecondary,
    targetProgress: Float = 1f,
    durationMillis: Int = 1000
) {
    var progress by remember { mutableStateOf(0f) }
    val progressAnimation by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing)
    )
    Canvas(modifier = modifier.height(9.dp)) {
        // Background indicator
        drawLine(
            color = backgroundColor,
            cap = StrokeCap.Round,
            strokeWidth = size.height,
            start = Offset(x = 0f, y = height.toPx() / 2),
            end = Offset(x = size.width, y = height.toPx() / 2)
        )
        // Foreground indicator
        var previousPercent = 1f
        colorPoints.reversed().forEach { (percent, color) ->
            drawLine(
                color = color,
                cap = StrokeCap.Round,
                strokeWidth = size.height,
                start = Offset(
                    x = (previousPercent - percent) * size.width * progressAnimation,
                    y = height.toPx() / 2
                ),
                end = Offset(
                    x = (previousPercent) * progressAnimation * size.width,
                    y = height.toPx() / 2
                )
            )
            previousPercent -= percent
        }
    }
    LaunchedEffect(targetProgress) {
        progress = targetProgress
    }
}

@Preview
@Composable
private fun AnimatableProgressView_Preview() {
    StudyCardsTheme {
        AnimatableProgressView(
            targetProgress = 1f,
            durationMillis = 1000,
            modifier = Modifier
                .padding(top = 50.dp, start = 16.dp, end = 16.dp)
                .height(20.dp)
                .fillMaxWidth(),
            colorPoints = listOf(
                0.5f to StudyCardsTheme.colors.success,
                0.3f to StudyCardsTheme.colors.uncertainStrong,
                0.2f to StudyCardsTheme.colors.unknown,
            ).toWrapperList()
        )
    }
}