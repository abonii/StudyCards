package abm.co.feature.userattributes.userinterest

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithRipple
import abm.co.designsystem.theme.StudyCardsTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun UserInterestItem(
    userInterest: UserInterestUI,
    onClick: (UserInterestUI) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .drawBehind {
                drawRect(
                    when (userInterest.isSelected) {
                        true -> Color.White
                        false -> Color(0xFF_387CEE)
                    }
                )
            }
            .clickableWithRipple { onClick(userInterest) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            text = userInterest.title,
            style = StudyCardsTheme.typography.weight400Size16LineHeight20
                .copy(
                    fontWeight = when (userInterest.isSelected) {
                        true -> FontWeight.W500
                        false -> FontWeight.W400
                    }
                ),
            color = when (userInterest.isSelected) {
                false -> Color.White
                true -> Color(0xFF_387CEE)
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
