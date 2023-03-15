package abm.co.feature.home.component

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp

private val ExpandedPadding = 4.dp
private val CollapsedPadding = 8.dp

private val ExpandedIconWidth = 3.dp
private val CollapsedIconWidth = 4.dp

private val ExpandedIconHeight = 45.dp
private val CollapsedIconHeight = 30.dp

@Preview
@Composable
fun CollapsingToolbarCollapsedPreview() {
    StudyCardsTheme {
        HomeCollapsingToolbar(
            backgroundImageResId = abm.co.designsystem.R.drawable.image_finished,
            progress = 0f,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            minToolbarHeight = 0,
            maxToolbarHeight = 0
        )
    }
}

@Preview
@Composable
fun CollapsingToolbarHalfwayPreview() {
    StudyCardsTheme {
        HomeCollapsingToolbar(
            backgroundImageResId = abm.co.designsystem.R.drawable.image_finished,
            progress = 0.5f,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            minToolbarHeight = 0,
            maxToolbarHeight = 0
        )
    }
}

@Preview
@Composable
fun CollapsingToolbarExpandedPreview() {
    StudyCardsTheme {
        HomeCollapsingToolbar(
            backgroundImageResId = abm.co.designsystem.R.drawable.image_finished,
            progress = 1f,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            minToolbarHeight = 0,
            maxToolbarHeight = 0
        )
    }
}

@Composable
fun HomeCollapsingToolbar(
    @DrawableRes backgroundImageResId: Int,
    progress: Float,
    modifier: Modifier = Modifier,
    minToolbarHeight: Int,
    maxToolbarHeight: Int
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
            CollapsingToolbarLayout(progress = progress, minToolbarHeight = minToolbarHeight) {
                val iconHeight = with(LocalDensity.current) {
                    lerp(CollapsedIconHeight.toPx(), ExpandedIconHeight.toPx(), progress).toDp()
                }
                val iconWidth = with(LocalDensity.current) {
                    lerp(CollapsedIconWidth.toPx(), ExpandedIconWidth.toPx(), progress).toDp()
                }
                val drawerEndPadding = with(LocalDensity.current) {
                    lerp(CollapsedPadding.toPx(), ExpandedPadding.toPx(), progress).toDp()
                }
                Icon(
                    painter = painterResource(id = R.drawable.ic_drawer),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = drawerEndPadding)
                        .height(24.dp)
                        .wrapContentWidth(),
                    tint = StudyCardsTheme.colors.opposition
                )
                Text(
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = if (progress < 0.8f) (progress - 0.4f).coerceAtLeast(0f) else 1f
                        }
                        .wrapContentWidth(),
                    text = "Welcomе, Dimash",
                    style = StudyCardsTheme.typography.weight400Size16LineHeight20,
                    color = StudyCardsTheme.colors.textPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = if (progress < 0.8f) (progress - 0.2f).coerceAtLeast(0f) else 1f
                        }.wrapContentWidth(),
                    text = "Start learn English",
                    style = StudyCardsTheme.typography.weight600Size23LineHeight24,
                    color = StudyCardsTheme.colors.textPrimary,
                    textAlign = TextAlign.Center
                )
                val circleBorderColor = StudyCardsTheme.colors.primary
                Image(
                    painter = painterResource(id = R.drawable.flag_china),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = drawerEndPadding)
                        .size(iconHeight)
                        .drawBehind {
                            val iconWidthPx = iconWidth.toPx()
                            drawCircle(
                                color = circleBorderColor,
                                center = Offset(size.width / 2f, size.height / 2f),
                                radius = size.width / 2f - iconWidthPx + iconWidthPx,
                                style = Stroke(width = iconWidthPx)
                            )
                        }
                )
            }
        }
    }
}

@Composable
private fun CollapsingToolbarLayout(
    progress: Float,
    minToolbarHeight: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measures, constraints ->
        check(measures.size == 4)

        val placeables = measures.map {
            it.measure(constraints)
        }
        layout(
            width = constraints.maxWidth,
            height = constraints.maxHeight
        ) {
            val drawer = placeables[0]
            val welcome = placeables[1]
            val learningLanguage = placeables[2]
            val icon = placeables[3]
            drawer.place(x = 0, y = icon.height / 2 - drawer.height / 2)
            welcome.placeRelative(
                x = lerp(
                    start = constraints.maxWidth / 2 - welcome.width,
                    stop = 0,
                    fraction = progress
                ),
                y = lerp(
                    start = icon.height / 2 - welcome.height / 2,
                    stop = minToolbarHeight / 2 - drawer.height + icon.height + 6.dp.roundToPx(),
                    fraction = progress
                )
            )
            learningLanguage.placeRelative(
                x = lerp(
                    start = drawer.width,
                    stop = 0,
                    fraction = progress
                ),
                y = lerp(
                    start = icon.height / 2 - learningLanguage.height / 2,
                    stop = constraints.maxHeight - learningLanguage.height,
                    fraction = progress
                )
            )
            icon.placeRelative(
                x = constraints.maxWidth - icon.width, y = 0
            )
        }
    }
}
