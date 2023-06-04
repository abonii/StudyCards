package abm.co.feature.book.detailed.component

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage

private val ExpandedPadding = 4.dp
private val CollapsedPadding = 8.dp

@Composable
fun BookDetailedCollapsingToolbar(
    bookTitle: String,
    bookImage: String,
    backgroundImage: String,
    progress: Float,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = StudyCardsTheme.colors.backgroundPrimary
    val dividerColor = StudyCardsTheme.colors.stroke
    Box(
        modifier = modifier
            .drawBehind {
                drawRect(color = backgroundColor.copy(alpha = 1 - progress))
                drawRect(
                    color = dividerColor.copy(alpha = 1 - progress),
                    topLeft = Offset(x = 0F, y = size.height - 1)
                )
            }
            .fillMaxSize()
    ) {
        CollapsingToolbarLayout(
            modifier = Modifier,
            statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
            progress = progress
        ) {
            val backIconPadding = with(LocalDensity.current) {
                lerp(CollapsedPadding.toPx(), ExpandedPadding.toPx(), progress).toDp()
            }
            Box {
                AsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    model = backgroundImage,
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
                Box(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                0f to StudyCardsTheme.colors.backgroundPrimary.copy(alpha = 0f),
                                0.44f to StudyCardsTheme.colors.backgroundPrimary.copy(alpha = 0.5f),
                                1f to StudyCardsTheme.colors.backgroundPrimary.copy(alpha = 1f),
                            )
                        )
                        .matchParentSize()
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_left),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .padding(end = backIconPadding)
                    .height(24.dp)
                    .wrapContentWidth()
                    .clickableWithoutRipple(onClick = onBack)
            )
            Text(
                modifier = Modifier.wrapContentWidth(),
                text = bookTitle,
                style = StudyCardsTheme.typography.weight500Size16LineHeight20,
                color = Color.White,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth(
                        remember(progress) {
                            lerp(
                                start = 0.3f,
                                stop = 0.4f,
                                fraction = progress
                            )
                        }
                    )
                    .aspectRatio(
                        remember(progress) {
                            lerp(
                                start = 0.85f,
                                stop = 0.656f,
                                fraction = progress
                            )
                        }
                    ),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = StudyCardsTheme.colors.backgroundPrimary,
                elevation = 10.dp
            ) {
                AsyncImage(
                    modifier = Modifier.matchParentSize(),
                    model = bookImage,
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun CollapsingToolbarLayout(
    statusBarHeight: Dp,
    progress: Float,
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
            val background = placeables[0]
            val backIcon = placeables[1]
            val title = placeables[2]
            val image = placeables[3]
            background.place(x = 0, y = 0)
            backIcon.place(
                x = 16.dp.roundToPx(),
                y = 10.dp.roundToPx() + statusBarHeight.roundToPx()
            )
            title.placeRelative(
                x = lerp(
                    start = backIcon.width + 24.dp.roundToPx(),
                    stop = 16.dp.roundToPx(),
                    fraction = progress
                ),
                y = lerp(
                    start = backIcon.height / 2
                            - title.height / 2
                            + statusBarHeight.roundToPx()
                            + 10.dp.roundToPx(),
                    stop = 10.dp.roundToPx()
                            + statusBarHeight.roundToPx()
                            + 10.dp.roundToPx()
                            + backIcon.height,
                    fraction = progress
                )
            )
            image.placeRelative(
                x = lerp(
                    start = constraints.maxWidth / 2 - image.width / 2,
                    stop = 16.dp.roundToPx(),
                    fraction = progress
                ),
                y = lerp(
                    start = backIcon.height / 2
                            - title.height / 2
                            + statusBarHeight.roundToPx()
                            + 10.dp.roundToPx()
                            + 37.dp.roundToPx(),
                    stop = 10.dp.roundToPx()
                            + statusBarHeight.roundToPx()
                            + 10.dp.roundToPx()
                            + backIcon.height
                            + title.height
                            + 10.dp.roundToPx(),
                    fraction = progress
                )
            )
        }
    }
}
