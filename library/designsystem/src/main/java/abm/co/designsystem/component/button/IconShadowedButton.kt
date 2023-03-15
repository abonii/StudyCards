package abm.co.designsystem.component.button

import abm.co.designsystem.theme.StudyCardsTheme
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Composable
fun IconShadowedButton(
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = StudyCardsTheme.colors.backgroundPrimary,
    shadowColor: Color = StudyCardsTheme.colors.selfish.copy(alpha = 0.3f),
    iconSize: DpSize = DpSize(24.dp, 37.dp),
    shape: Shape = RoundedCornerShape(8.dp),
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 20.dp,
                shape = shape,
                spotColor = shadowColor,
                ambientColor = shadowColor
            )
            .clip(shape)
            .background(backgroundColor)
            .clickable(onClick = { onClick?.invoke() }),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(iconSize),
            painter = painterResource(id = iconRes),
            contentDescription = null
        )
    }
}
