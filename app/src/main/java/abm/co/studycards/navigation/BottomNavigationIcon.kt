package abm.co.studycards.navigation

import abm.co.designsystem.component.modifier.scalableClick
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.studycards.R
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Immutable
data class BottomNavigationItem(
    @StringRes val nameRes: Int?,
    @IdRes val resId: Int,
    @DrawableRes val iconRes: Int,
    val badgeCount: Int = 0,
    val order: Int
)

@Composable
fun BottomNavigationIcon(
    item: BottomNavigationItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .scalableClick(
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val color = when {
            item.iconRes == R.drawable.ic_new_card -> null
            selected -> {
                StudyCardsTheme.colors.buttonPrimary
            }

            else -> {
                StudyCardsTheme.colors.grayishBlue
            }
        }
        Image(
            modifier = Modifier,
            painter = painterResource(id = item.iconRes),
            colorFilter = color?.let { ColorFilter.tint(it) },
            contentDescription = null
        )
        item.nameRes?.let {
            Text(
                text = stringResource(id = it),
                style = StudyCardsTheme.typography.weight400Size12LineHeight16,
                textAlign = TextAlign.Center,
                color = color ?: Color.Unspecified
            )
        }
    }
}