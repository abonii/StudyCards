package abm.co.designsystem.component.toolbar

import abm.co.designsystem.R
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.theme.StudyCardsTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private val TOOLBAR_HEIGHT = 56.dp

@Composable
fun Toolbar(
    title: String,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
    contentColor: Color = StudyCardsTheme.colors.opposition,
    onBack: (() -> Unit)? = null,
) {
    val showToolbarElevation by remember(lazyListState) {
        derivedStateOf {
            lazyListState.firstVisibleItemScrollOffset != 0 ||
                    lazyListState.firstVisibleItemIndex != 0
        }
    }
    Toolbar(
        modifier = modifier,
        title = title,
        showElevation = showToolbarElevation,
        contentColor = contentColor,
        onBack = onBack
    )
}

@Composable
fun Toolbar(
    title: String,
    modifier: Modifier = Modifier,
    contentColor: Color = StudyCardsTheme.colors.opposition,
    showElevation: Boolean = false,
    onBack: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .height(TOOLBAR_HEIGHT)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .padding(start = 6.dp, end = 16.dp)
                .fillMaxWidth()
                .weight(1f)
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
                modifier = Modifier
                    .align(Alignment.Center)
                        then if (onBack != null)
                            Modifier.padding(horizontal = 34.dp) else Modifier,
                text = title,
                style = StudyCardsTheme.typography.weight600Size14LineHeight18,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
        if (showElevation) {
            Divider(
                modifier = androidx.compose.ui.Modifier.shadow(elevation = 1.dp, clip = false),
                color = colorResource(id = R.color.fill_divider),
                thickness = 0.dp
            )
        }
    }
}
