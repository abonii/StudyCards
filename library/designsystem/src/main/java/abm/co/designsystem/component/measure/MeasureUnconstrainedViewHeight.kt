package abm.co.designsystem.component.measure

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp

@Composable
fun MeasureUnconstrainedViewHeight(
    viewToMeasure: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (maxHeight: Dp, maxWidth: Dp) -> Unit
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val view = subcompose(
            slotId = "viewToMeasure",
            content = viewToMeasure
        )[0].measure(
            constraints = Constraints(maxWidth = constraints.maxWidth)
        )
        val maxHeight = view.height.toDp()
        val maxWidth = view.width.toDp()

        val contentPlaceable = subcompose(
            slotId = "content",
            content = {
                content(maxHeight, maxWidth)
            }
        )[0].measure(
            constraints = constraints
        )
        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.place(0, 0)
        }
    }
}


@Composable
fun MeasureUnconstrainedViewHeight(
    viewsToMeasure: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier,
    content: @Composable (maxHeight: Dp) -> Unit
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        var index = 0
        val maxHeight = viewsToMeasure.maxOf { viewToMeasure ->
            return@maxOf subcompose(
                slotId = "viewToMeasure-${index++}",
                content = viewToMeasure
            )[0].measure(
                constraints = Constraints(maxWidth = constraints.maxWidth)
            ).height.toDp()
        }

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
