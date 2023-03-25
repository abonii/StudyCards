package abm.co.feature.game.swipe.drag

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

fun Modifier.moveTo(
    x: Float,
    y: Float
) = this.then(Modifier.layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(placeable.width, placeable.height) {
        placeable.placeRelative(x.roundToInt(), y.roundToInt())
    }
})

fun Modifier.shadowPadding(
    margin: Dp,
    draggableCardController: DraggableCardController,
    layer: Float
) = this.padding(
    all = ((layer + (10 - draggableCardController.scale.value * 10)) * margin.value).dp
)
