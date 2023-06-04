package abm.co.designsystem.component.about

import abm.co.designsystem.R
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.theme.StudyCardsTheme
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * this view used instead of [com.codebusters.shared.theme.button.TextButton]
 * in order to animate end icon
 */
@Composable
fun ExpandCollapseButton(
    isExpanded: Boolean,
    collapsedText: String,
    expandedText: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val dropDownScale by animateFloatAsState(
        targetValue = if (isExpanded) {
            -1F
        } else {
            1F
        }
    )
    Row(
        modifier = modifier
            .clickableWithoutRipple(onClick = onClick)
            .height(36.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isExpanded) {
                expandedText
            } else {
                collapsedText
            },
            style = StudyCardsTheme.typography.weight500Size16LineHeight20,
            color = StudyCardsTheme.colors.primary
        )
        Spacer(modifier = Modifier.width(6.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = "drop down icon",
            modifier = Modifier
                .graphicsLayer {
                    scaleY = dropDownScale
                }
                .rotate(degrees = 90f)
                .size(16.dp),
            colorFilter = ColorFilter.tint(color = StudyCardsTheme.colors.primary)
        )
    }
}
