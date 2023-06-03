package abm.co.feature.book.library.component

import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun LibraryKindTitle(
    title: String,
    modifier: Modifier = Modifier,
    showArrow: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .weight(1f),
            text = title,
            style = StudyCardsTheme.typography.weight600Size16LineHeight18,
            color = StudyCardsTheme.colors.textPrimary
        )
        if (showArrow) {
            Icon(
                painter = painterResource(id = abm.co.designsystem.R.drawable.ic_chevron_right),
                contentDescription = null
            )
        }
    }
}
