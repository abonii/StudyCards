package abm.co.designsystem.component.button

import abm.co.designsystem.theme.StudyCardsTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableInferredTarget
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@NonRestartableComposable
@Composable
fun TextButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = StudyCardsTheme.typography.weight500Size14LineHeight20,
    buttonState: ButtonState = remember { ButtonState.Normal },
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    components: ButtonSize = ButtonSize(),
    normalContentColor: Color = StudyCardsTheme.colors.skyBlue,
    disabledButtonContentColor: Color = StudyCardsTheme.colors.silver
) {
    val isPressed by interactionSource.collectIsPressedAsState()

    val contentColor = if (buttonState == ButtonState.Disabled) {
        disabledButtonContentColor
    } else {
        if (isPressed) normalContentColor.copy(alpha = 0.5f)
        else normalContentColor
    }
    Box(
        modifier = modifier
            .padding(10.dp)
            .clickable(
                indication = null,
                interactionSource = interactionSource,
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