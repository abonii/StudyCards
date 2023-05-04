package abm.co.designsystem.component.button

import abm.co.designsystem.theme.StudyCardsTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun SecondaryButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = StudyCardsTheme.typography.weight600Size14LineHeight18,
    buttonState: ButtonState = ButtonState.Normal,
    components: ButtonSize = ButtonSize(),
    normalButtonBackgroundColor: Color = StudyCardsTheme.colors.backgroundPrimary,
    normalContentColor: Color = StudyCardsTheme.colors.textPrimary,
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
            .border(
                width = 1.dp,
                color = StudyCardsTheme.colors.backgroundSecondary,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(components.radius)
            )
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
