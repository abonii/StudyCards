package abm.co.designsystem.component.modifier

import abm.co.designsystem.theme.StudyCardsTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color

fun Modifier.clickableWithRipple(
    normalColor: Color = Color.Transparent,
    pressedColor: Color? = null,
    onClick: () -> Unit
): Modifier = composed {
    composed {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val backgroundColor = if (isPressed) {
            pressedColor ?: StudyCardsTheme.colors.pressed
        } else {
            normalColor
        }
        clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        ).drawBehind {
            drawRect(backgroundColor)
        }
    }
}
