package abm.co.feature.card.category.component

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.theme.StudyCardsTheme
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp

private val ExpandedPadding = 4.dp
private val CollapsedPadding = 8.dp

private val ExpandedIconHeight = 32.dp
private val CollapsedIconHeight = 30.dp

@Composable
fun CategoryCollapsingToolbar(
    title: String,
    subtitle: String,
    progress: Float,
    @DrawableRes endIconRes: Int,
    onClickEndIcon: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = StudyCardsTheme.colors.backgroundPrimary
    val dividerColor = StudyCardsTheme.colors.stroke
    Column(
        modifier = modifier
            .drawBehind {
                drawRect(color = backgroundColor.copy(alpha = 1 - progress))
                drawRect(
                    color = dividerColor.copy(alpha = 1 - progress),
                    topLeft = Offset(x = 0F, y = size.height - 1)
                )
            }
            .fillMaxSize()
            .padding(top = 18.dp)
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxSize(),
        ) {
            CollapsingToolbarLayout(progress = progress) {
                val iconHeight = with(LocalDensity.current) {
                    lerp(CollapsedIconHeight.toPx(), ExpandedIconHeight.toPx(), progress).toDp()
                }
                val drawerEndPadding = with(LocalDensity.current) {
                    lerp(CollapsedPadding.toPx(), ExpandedPadding.toPx(), progress).toDp()
                }
                Text(
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = if (progress < 0.8f)
                                (progress - 0.4f).coerceAtLeast(0f)
                            else 1f
                        }
                        .wrapContentWidth(),
                    text = title,
                    style = StudyCardsTheme.typography.weight400Size16LineHeight20,
                    color = StudyCardsTheme.colors.textPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.wrapContentWidth(),
                    text = subtitle,
                    style = StudyCardsTheme.typography.weight600Size23LineHeight24,
                    color = StudyCardsTheme.colors.textPrimary,
                    textAlign = TextAlign.Center
                )
                Image(
                    painter = painterResource(id = endIconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = drawerEndPadding)
                        .size(iconHeight)
                        .clickableWithoutRipple(onClick = onClickEndIcon)
                )
            }
        }
    }
}

@Composable
private fun CollapsingToolbarLayout(
    progress: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measures, constraints ->
        check(measures.size == 3)

        val placeables = measures.map {
            it.measure(constraints)
        }
        layout(
            width = constraints.maxWidth,
            height = constraints.maxHeight
        ) {
            val title = placeables[0]
            val subtitle = placeables[1]
            val endIcon = placeables[2]
            title.place(x = 0, y = 0)
            subtitle.placeRelative(
                x = lerp(
                    start = constraints.maxWidth / 2 - subtitle.width,
                    stop = 0,
                    fraction = progress
                ),
                y = lerp(
                    start = title.height / 2 - subtitle.height / 2,
                    stop = constraints.maxHeight - endIcon.height - subtitle.height - 10.dp.roundToPx(),
                    fraction = progress
                )
            )
            endIcon.placeRelative(
                x = constraints.maxWidth - endIcon.width, y = 0
            )
        }
    }
}
