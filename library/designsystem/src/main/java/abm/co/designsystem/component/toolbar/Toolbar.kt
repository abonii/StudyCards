package abm.co.designsystem.component.toolbar

import abm.co.designsystem.R
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.theme.StudyCardsTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private val TOOLBAR_HEIGHT = 56.dp

@Composable
fun Toolbar(
    title: String,
    modifier: Modifier = Modifier,
    contentColor: Color = StudyCardsTheme.colors.opposition,
    onBack: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .height(TOOLBAR_HEIGHT)
            .fillMaxWidth()
            .padding(start = 6.dp, end = 16.dp)
    ) {
        onBack?.let {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickableWithoutRipple(onBack)
                    .padding(10.dp)
                    .size(24.dp),
                painter = painterResource(id = R.drawable.ic_left),
                tint = contentColor,
                contentDescription = null
            )
        }
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = title,
            style = StudyCardsTheme.typography.weight600Size14LineHeight18,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
