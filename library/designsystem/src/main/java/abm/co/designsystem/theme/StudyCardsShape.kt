package abm.co.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class StudyCardsShape(
    val shapeSmall: Shape = RoundedCornerShape(4.dp),
    val shapeMedium: Shape = RoundedCornerShape(8.dp),
    val shapeBig: Shape = RoundedCornerShape(12.dp)
)

internal val LocalShapes = staticCompositionLocalOf { StudyCardsShape() }
