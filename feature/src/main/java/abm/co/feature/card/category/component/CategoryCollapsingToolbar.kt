package abm.co.feature.card.category.component

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import kotlin.math.roundToInt

private val ExpandedPadding = 4.dp
private val CollapsedPadding = 8.dp

private val ExpandedIconHeight = 32.dp
private val CollapsedIconHeight = 30.dp

@Composable
fun CategoryCollapsingToolbar(
    title: String,
    subtitle: String,
    progress: Float,
    @DrawableRes addCardIconRes: Int,
    @DrawableRes changeCategoryIconRes: Int,
    onBack: () -> Unit,
    onChangeTitle: () -> Unit,
    onClickAddCardIcon: () -> Unit,
    onClickChangeCategoryIcon: () -> Unit,
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
            .padding(top = 10.dp)
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
                Icon(
                    modifier = Modifier
                        .clickableWithoutRipple(onBack)
                        .padding(end = 10.dp, top = 10.dp, bottom = 10.dp)
                        .size(24.dp),
                    painter = painterResource(id = R.drawable.ic_left),
                    contentDescription = null,
                    tint = StudyCardsTheme.colors.opposition
                )
                Text(
                    modifier = Modifier
                        .absoluteOffset(y = (-1).dp)
                        .wrapContentWidth()
                        .clickableWithoutRipple(onClick = onChangeTitle),
                    text = title,
                    style = StudyCardsTheme.typography.weight600Size23LineHeight24
                        .copy(
                            letterSpacing = ((1 - progress) * 1.5).sp,
                            fontSize = (StudyCardsTheme
                                .typography
                                .weight600Size23LineHeight24
                                .fontSize.value + ((1 - progress) * 2)).sp
                        ),
                    color = StudyCardsTheme.colors.textPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = if (progress < 0.8f)
                                (progress - 0.4f).coerceAtLeast(0f)
                            else 1f
                        }
                        .wrapContentWidth(),
                    text = subtitle,
                    style = StudyCardsTheme.typography.weight400Size16LineHeight20,
                    color = StudyCardsTheme.colors.textPrimary,
                    textAlign = TextAlign.Center
                )
                Image(
                    painter = painterResource(id = changeCategoryIconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = drawerEndPadding)
                        .size(iconHeight)
                        .clickableWithoutRipple(onClick = onClickChangeCategoryIcon)
                )
                Image(
                    painter = painterResource(id = addCardIconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = drawerEndPadding)
                        .size(iconHeight)
                        .clickableWithoutRipple(onClick = onClickAddCardIcon)
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
        check(measures.size == 5)

        val placeables = measures.map {
            it.measure(constraints)
        }
        layout(
            width = constraints.maxWidth,
            height = constraints.maxHeight
        ) {
            val back = placeables[0]
            val title = placeables[1]
            val subtitle = placeables[2]
            val changeCategoryIcon = placeables[3]
            val addCardIcon = placeables[4]
            back.place(
                x = 0,
                y = 0
            )
            title.placeRelative(
                x = lerp(
                    start = back.width - 5.dp.roundToPx(),
                    stop = back.width,
                    fraction = progress
                ),
                y = back.height / 2 - title.height / 2
            )
            subtitle.placeRelative(
                x = 0,
                y = lerp(
                    start = title.height / 2 - subtitle.height / 2,
                    stop = constraints.maxHeight - (subtitle.height * 1.5).roundToInt(),
                    fraction = progress
                )
            )
            changeCategoryIcon.placeRelative(
                x = constraints.maxWidth - addCardIcon.width - changeCategoryIcon.width - 10.dp.roundToPx(),
                y = back.height / 2 - title.height / 2
            )
            addCardIcon.placeRelative(
                x = constraints.maxWidth - addCardIcon.width,
                y = back.height / 2 - title.height / 2
            )
        }
    }
}
