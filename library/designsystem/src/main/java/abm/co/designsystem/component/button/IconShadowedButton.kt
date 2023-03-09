package abm.co.designsystem.component.button

import abm.co.designsystem.R
import abm.co.designsystem.theme.StudyCardsTheme
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IconShadowedButton(
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 20.dp,
                shape = shape,
                spotColor = StudyCardsTheme.colors.selfish.copy(alpha = 0.3f),
                ambientColor = StudyCardsTheme.colors.selfish.copy(alpha = 0.3f)
            )
            .clip(shape)
            .background(StudyCardsTheme.colors.backgroundPrimary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .padding(vertical = 13.dp)
                .size(iconSize),
            painter = painterResource(id = iconRes),
            contentDescription = null
        )
    }
}
