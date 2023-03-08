package abm.co.designsystem.component.button

import abm.co.designsystem.R
import abm.co.designsystem.theme.StudyCardsTypography
import androidx.annotation.ColorRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class ButtonComponents(
    @ColorRes val normalButtonBackgroundColor: Int = R.color.button_item_text_button,
    @ColorRes val normalContentColor: Int = R.color.nonadaptable_white,
    @ColorRes val disabledButtonBackgroundColor: Int = R.color.button_item_disabled,
    @ColorRes val disabledButtonContentColor: Int = R.color.text_primary,
    @ColorRes val loadingButtonBackgroundColor: Int = R.color.button_item_text_button,
    @ColorRes val loadingButtonContentColor: Int = R.color.nonadaptable_white,
    val loaderSize: Dp = 14.dp,
    val textStyle: TextStyle = StudyCardsTypography.wight600Size14LineHeight18,
    val height: Dp = 50.dp,
    val radius: Dp = 12.dp,
    val isFixed: Boolean = true
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
    buttonState: ButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    components: ButtonComponents = ButtonComponents()
) {
    val backgroundColor = when (buttonState) {
        ButtonState.Loading -> colorResource(id = components.loadingButtonBackgroundColor)
        ButtonState.Disabled -> colorResource(id = components.disabledButtonBackgroundColor)
        ButtonState.Normal -> colorResource(id = components.normalButtonBackgroundColor)
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(components.radius))
            .background(backgroundColor)
            .height(components.height)
            .run {
                if(components.isFixed){
                    fillMaxWidth()
                } else this
            }
            .clickable(
                onClick = {
                    if (buttonState == ButtonState.Normal) {
                        onClick()
                    }
                }),
        contentAlignment = Alignment.Center
    ) {
        when (buttonState) {
            ButtonState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(components.loaderSize),
                    color = colorResource(id = components.loadingButtonContentColor),
                    strokeWidth = 2.dp
                )
            }
            ButtonState.Disabled -> {
                Text(
                    text = title,
                    style = components.textStyle.copy(
                        colorResource(id = components.disabledButtonContentColor)
                    ),
                    maxLines = 1
                )
            }
            ButtonState.Normal -> {
                Text(
                    text = title,
                    style = components.textStyle.copy(
                        colorResource(id = components.normalContentColor)
                    ),
                    maxLines = 1
                )
            }
        }
    }
}