package abm.co.feature.game.swipe

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
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

fun Modifier.shadowHorizontalPadding(
    margin: Dp,
    cardStackController: CardStackController,
    layer: Float
) = this.padding(
    all = ((layer + (10 - cardStackController.scale.value * 10)) * margin.value).dp
)

fun Modifier.drawSwipeSideBehind(
    cardStackController: CardStackController
) = this.drawBehind {
    if (cardStackController.offsetX.value > 0) {
        drawPath(
            path = pathForRight(cardStackController.offsetX.value),
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF_46A642).copy(alpha = 0.5f),
                    Color(0xFF_46A642).copy(alpha = 0.5f),
                    Color(0xFF_46A642).copy(alpha = 0f)
                )
            )
        )
    }
    if (cardStackController.offsetX.value < 0) {
        drawPath(
            path = pathForLeft(cardStackController.offsetX.value),
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF_FF453A).copy(alpha = 0.5f),
                    Color(0xFF_FF453A).copy(alpha = 0.5f),
                    Color(0xFF_FF453A).copy(alpha = 0f)
                )
            )
        )
    }
    if (cardStackController.offsetY.value > 0) {
        drawPath(
            path = pathForBottom(cardStackController.offsetY.value),
            brush = object : ShaderBrush() {
                override fun createShader(size: Size): Shader {
                    return LinearGradientShader(
                        colors = listOf(
                            Color(0xFF_CFB323).copy(alpha = 0.5f),
                            Color(0xFF_CFB323).copy(alpha = 0.3f),
                            Color(0xFF_CFB323).copy(alpha = 0.1f),
                        ),
                        from = Offset.Zero,
                        to = Offset(size.width, size.height)
                    )
                }
            }
        )
    }
}

private fun DrawScope.pathForRight(
    offsetX: Float
): Path {
    val path = Path()
    path.moveTo(x = size.width - 15.dp.toPx(), y = 10.dp.toPx())
    path.lineTo(
        x = size.width - 15.dp.toPx() + offsetX,
        y = 10.dp.toPx()
    )
    path.lineTo(
        x = size.width + offsetX,
        y = size.height
    )
    path.lineTo(size.width, y = size.height)
    path.close()
    return path
}

private fun DrawScope.pathForLeft(
    offsetX: Float
): Path {
    val path = Path()
    path.moveTo(x = 15.dp.toPx(), y = 10.dp.toPx())
    path.lineTo(x = 15.dp.toPx() + offsetX, y = 10.dp.toPx())
    path.lineTo(x = offsetX, y = size.height)
    path.lineTo(0f, y = size.height)
    path.close()
    return path
}

private fun DrawScope.pathForBottom(
    offsetY: Float
): Path {
    val path = Path()
    path.moveTo(x = 5.dp.toPx(), y = size.height - 5.dp.toPx())
    path.lineTo(x = 5.dp.toPx(), y = size.height - 5.dp.toPx() + offsetY)
    path.lineTo(x = size.width - 5.dp.toPx(), y = size.height - 5.dp.toPx() + offsetY)
    path.lineTo(x = size.width - 5.dp.toPx(), y = size.height - 5.dp.toPx())
    path.close()
    return path
}