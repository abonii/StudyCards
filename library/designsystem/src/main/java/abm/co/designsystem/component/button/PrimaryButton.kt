package abm.co.designsystem.component.button

import abm.co.designsystem.theme.StudyCardsTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class ButtonSize(
    val loaderSize: Dp = 14.dp,
    val height: Dp = 50.dp,
    val radius: Dp = 12.dp
)

@Immutable
enum class ButtonState {
    Loading,
    Disabled,
    Normal
}

@Composable
fun PrimaryButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = StudyCardsTheme.typography.weight600Size14LineHeight18,
    buttonState: ButtonState = ButtonState.Normal,
    components: ButtonSize = ButtonSize(),
    normalButtonBackgroundColor: Color = StudyCardsTheme.colors.buttonPrimary,
    normalContentColor: Color = Color.White,
    disabledButtonBackgroundColor: Color = StudyCardsTheme.colors.buttonDisabled,
    disabledButtonContentColor: Color = StudyCardsTheme.colors.textPrimary,
) {
    val backgroundColor = when (buttonState) {
        ButtonState.Loading, ButtonState.Normal -> normalButtonBackgroundColor
        ButtonState.Disabled -> disabledButtonBackgroundColor
    }
    val contentColor = when (buttonState) {
        ButtonState.Loading, ButtonState.Normal -> normalContentColor
        ButtonState.Disabled -> disabledButtonContentColor
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(components.radius))
            .background(backgroundColor)
            .height(components.height)
            .clickable(
                onClick = {
                    if (buttonState == ButtonState.Normal) {
                        onClick()
                    }
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        when (buttonState) {
            ButtonState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(components.loaderSize),
                    color = contentColor,
                    strokeWidth = 2.dp
                )
            }
            ButtonState.Disabled -> {
                Text(
                    text = title,
                    style = textStyle.copy(color = contentColor),
                    maxLines = 1
                )
            }
            ButtonState.Normal -> {
                Text(
                    text = title,
                    style = textStyle.copy(color = contentColor),
                    maxLines = 1
                )
            }
        }
    }
}
