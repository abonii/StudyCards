package abm.co.feature.game.swipe

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp

@Composable
fun MeasureUnconstrainedViewHeight(
    viewToMeasure: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (maxHeight: Dp) -> Unit
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val maxHeight = subcompose(
            slotId = "viewToMeasure",
            content = viewToMeasure
        )[0].measure(
            constraints = Constraints(maxWidth = constraints.maxWidth)
        ).height.toDp()

        val contentPlaceable = subcompose(
            slotId = "content",
            content = {
                content(maxHeight)
            }
        )[0].measure(
            constraints = constraints
        )
        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.place(0, 0)
        }
    }
}
