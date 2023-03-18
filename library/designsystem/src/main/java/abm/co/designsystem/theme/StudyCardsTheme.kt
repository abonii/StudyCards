package abm.co.designsystem.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember

@Composable
fun StudyCardsTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColors
    } else {
        LightColors
    }

    StudyCardsThemeImpl(
        colors = colors,
        content = content
    )
}

@Composable
private fun StudyCardsThemeImpl(
    shapes: StudyCardsShape = StudyCardsTheme.shapes,
    typography: StudyCardsTypography = StudyCardsTheme.typography,
    colors: StudyCardsColor = StudyCardsTheme.colors,
    content: @Composable () -> Unit,
) {
    val rememberedColors = remember {
        colors.copy()
    }.apply { updateColorsFrom(colors) }
    val rippleIndication = rememberRipple()
    CompositionLocalProvider(
        LocalColors provides rememberedColors,
        LocalContentAlpha provides ContentAlpha.high,
        LocalShapes provides shapes,
        LocalIndication provides rippleIndication,
        LocalRippleTheme provides StudyCardsRippleTheme,
        LocalTypography provides typography
    ) {
        ProvideTextStyle(value = typography.weight500Size14LineHeight20, content)
    }
}

object StudyCardsTheme {
    val colors: StudyCardsColor
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val typography: StudyCardsTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current

    val shapes: StudyCardsShape
        @Composable
        @ReadOnlyComposable
        get() = LocalShapes.current
}

@Immutable
private object StudyCardsRippleTheme : RippleTheme {

    @Composable
    override fun defaultColor() = RippleTheme.defaultRippleColor(
        contentColor = LocalContentColor.current,
        lightTheme = MaterialTheme.colors.isLight
    )

    @Composable
    override fun rippleAlpha() = RippleTheme.defaultRippleAlpha(
        contentColor = LocalContentColor.current,
        lightTheme = MaterialTheme.colors.isLight
    )
}
