package abm.co.designsystem.component.button

import abm.co.designsystem.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp

@Composable
fun TextButton(
    title: String,
    buttonState: ButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    components: ButtonComponents = ButtonComponents()
) {
    val isPressed by interactionSource.collectIsPressedAsState()

    val contentColor = if (buttonState == ButtonState.Disabled) {
        colorResource(id = R.color.text_tertiary)
    } else {
        if (isPressed) colorResource(id = components.normalContentColor).copy(alpha = 0.5f)
        else colorResource(id = components.normalContentColor)
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
                    style = components.textStyle.copy(color = contentColor),
                    maxLines = 1
                )
            }
            ButtonState.Normal -> {
                Text(
                    text = title,
                    style = components.textStyle.copy(color = contentColor),
                    maxLines = 1
                )
            }
        }
    }
}