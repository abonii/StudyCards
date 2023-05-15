package abm.co.designsystem.component.button

import abm.co.designsystem.component.modifier.safeClickable
import abm.co.designsystem.theme.StudyCardsTheme
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Composable
fun IconButton(
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonState: ButtonState = ButtonState.Normal,
    contentColor: Color = StudyCardsTheme.colors.blueMiddle,
    iconSize: DpSize = DpSize(24.dp, 24.dp)
) {
    val updatedOnClick by rememberUpdatedState(newValue = onClick)
    Box(
        modifier = modifier
            .clip(CircleShape)
            .safeClickable(onClick = {
                if (buttonState == ButtonState.Normal) {
                    updatedOnClick()
                }
            })
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        when (buttonState) {
            ButtonState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(iconSize),
                    color = contentColor,
                    strokeWidth = 2.dp
                )
            }

            ButtonState.Disabled -> {
                Icon(
                    modifier = Modifier.size(iconSize),
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = StudyCardsTheme.colors.buttonDisabled
                )
            }

            ButtonState.Normal -> {
                Icon(
                    modifier = Modifier.size(iconSize),
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = contentColor
                )
            }
        }
    }
}
