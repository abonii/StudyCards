package abm.co.feature.home.component

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.domain.functional.safeLet
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp

private val ExpandedPadding = 4.dp
private val CollapsedPadding = 8.dp

private val ExpandedIconWidth = 3.dp
private val CollapsedIconWidth = 4.dp

private val ExpandedIconHeight = 38.dp
private val CollapsedIconHeight = 26.dp

@Composable
fun HomeCollapsingToolbar(
    welcomeText: String,
    learningLanguageText: String,
    @DrawableRes learningLanguageRes: Int?,
    @DrawableRes nativeLanguageRes: Int?,
    toolbarTitle: String,
    progress: Float,
    onClickDrawerIcon: () -> Unit,
    onClickLearningLanguageIcon: () -> Unit,
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
                val iconSize = with(LocalDensity.current) {
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
                        .wrapContentWidth()
                        .clickableWithoutRipple(onClick = onClickDrawerIcon),
                    tint = StudyCardsTheme.colors.opposition
                )
                Text(
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = if (progress < 0.8f) (progress - 0.4f).coerceAtLeast(0f) else 1f
                        }
                        .wrapContentWidth(),
                    text = welcomeText,
                    style = StudyCardsTheme.typography.weight400Size16LineHeight20,
                    color = StudyCardsTheme.colors.textPrimary,
                    textAlign = TextAlign.Center
                )
                val animate by remember(progress) { derivedStateOf { progress < 0.4f } }
                val learningLanguageTransition =
                    updateTransition(targetState = animate, label = "learningLanguageTransition")
                val learningLanguageAlpha by learningLanguageTransition.animateFloat(
                    label = "learningLanguageAlpha",
                    targetValueByState = {
                        if (it) 1f - progress else progress
                    })
                Crossfade(
                    targetState = animate,
                    modifier = Modifier.graphicsLayer {
                        alpha = learningLanguageAlpha
                    },
                    animationSpec = tween(easing = CubicBezierEasing(0.12f, 0f, 0.12f, 1f)),
                    label = "home collapsing toolbar"
                ) {
                    Text(
                        modifier = Modifier.wrapContentWidth(),
                        text = if (it) toolbarTitle else learningLanguageText,
                        style = StudyCardsTheme.typography.weight600Size23LineHeight24,
                        color = StudyCardsTheme.colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                }
                safeLet(nativeLanguageRes, learningLanguageRes) { native, learning ->
                    val circleBorderColor = StudyCardsTheme.colors.primary
                    Box(
                        modifier = Modifier
                            .padding(start = drawerEndPadding)
                            .clickableWithoutRipple(onClick = onClickLearningLanguageIcon)
                    ) {
                        Image(
                            painter = painterResource(id = native),
                            contentDescription = null,
                            modifier = Modifier
                                .offset(
                                    x = -iconSize / 6,
                                    y = -iconSize / 8
                                )
                                .size(iconSize)
                        )
                        Image(
                            painter = painterResource(id = learning),
                            contentDescription = null,
                            modifier = Modifier
                                .offset(
                                    x = iconSize / 6,
                                    y = iconSize / 6
                                )
                                .size(iconSize)
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
                } ?: Spacer(
                    modifier = Modifier
                        .padding(start = drawerEndPadding)
                        .size(iconSize)
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
                    stop = constraints.maxHeight - learningLanguage.height - welcome.height - 10.dp.roundToPx(),
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
