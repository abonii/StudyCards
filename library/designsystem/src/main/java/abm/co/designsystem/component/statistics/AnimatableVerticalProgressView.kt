package abm.co.designsystem.component.statistics

import abm.co.designsystem.theme.StudyCardsTheme
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimatableVerticalProgressView(
    percent: Float,
    modifier: Modifier = Modifier,
    width: Dp = 6.dp,
    durationMillis: Int = 1000,
    brush: Brush = Brush.horizontalGradient(
        colors = remember { listOf(Color(0xFF_529DFF), Color(0xFF_3579F5)) }
    )
) {
    var progress by remember { mutableStateOf(0f) }
    val progressAnimation by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing)
    )
    Canvas(modifier = modifier.width(width)) {
        // Foreground indicator
        drawLine(
            brush = brush,
            cap = StrokeCap.Round,
            strokeWidth = size.width,
            start = Offset(x = width.toPx() / 2, y = size.height),
            end = Offset(
                x = width.toPx() / 2,
                y = size.height * (1 - progressAnimation)
            )
        )
    }
    LaunchedEffect(percent) {
        progress = percent
    }
}

@Preview(name = "Phone", device = "spec:width=411dp,height=891dp")
@Preview
@Composable
private fun AnimatableProgressView_Preview() {
    StudyCardsTheme {
        Box(modifier = Modifier.height(200.dp)) {
            AnimatableVerticalProgressView(
                durationMillis = 4000,
                modifier = Modifier
                    .padding(top = 50.dp, start = 16.dp, end = 16.dp)
                    .background(StudyCardsTheme.colors.uncertainStrong)
                    .width(20.dp)
                    .fillMaxHeight(0.3f),
                percent = 0.5f
            )
        }
    }
}