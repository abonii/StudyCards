package abm.co.designsystem.theme

import abm.co.designsystem.R
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val defaultFontFamily: FontFamily = FontFamily(
    Font(
        resId = R.font.golos_text_black,
        weight = FontWeight.Black
    ),
    Font(
        resId = R.font.golos_text_bold,
        weight = FontWeight.Bold
    ),
    Font(
        resId = R.font.golos_text_demi_bold,
        weight = FontWeight.SemiBold
    ),
    Font(
        resId = R.font.golos_text_medium,
        weight = FontWeight.Medium
    ),
    Font(
        resId = R.font.golos_text_vf,
        weight = FontWeight.Light,
    ),
    Font(
        resId = R.font.golos_text_regular,
        weight = FontWeight.Normal
    )
)

@Immutable
data class StudyCardsTypography(
    val weight400Size16LineHeight20: TextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.W400,
        lineHeight = 20.sp
    ).withDefaultFontFamily(defaultFontFamily),

    val weight400Size20LineHeight20: TextStyle = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.W400,
        lineHeight = 20.sp
    ).withDefaultFontFamily(defaultFontFamily),

    val weight600Size23LineHeight24: TextStyle = TextStyle(
        fontSize = 23.sp,
        fontWeight = FontWeight.W600,
        lineHeight = 24.sp
    ).withDefaultFontFamily(defaultFontFamily),

    val weight600Size14LineHeight18: TextStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.W600,
        lineHeight = 18.sp
    ).withDefaultFontFamily(defaultFontFamily),

    val weight400Size14LineHeight20: TextStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.W400,
        lineHeight = 20.sp
    ).withDefaultFontFamily(defaultFontFamily),

    val weight400Size14LineHeight24: TextStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.W400,
        lineHeight = 24.sp
    ).withDefaultFontFamily(defaultFontFamily),

    val weight500Size14LineHeight20: TextStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.W500,
        lineHeight = 20.sp
    ).withDefaultFontFamily(defaultFontFamily),

    val weight500Size16LineHeight20: TextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.W500,
        lineHeight = 20.sp
    ).withDefaultFontFamily(defaultFontFamily),

    val weight400Size14LineHeight18: TextStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.W400,
        lineHeight = 17.sp
    ).withDefaultFontFamily(defaultFontFamily),

    val weight400Size12LineHeight16: TextStyle = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.W400,
        lineHeight = 16.sp
    ).withDefaultFontFamily(defaultFontFamily)
)

/**
 * @return [this] if there is a [FontFamily] defined, otherwise copies [this] with [default] as
 * the [FontFamily].
 */
private fun TextStyle.withDefaultFontFamily(default: FontFamily): TextStyle {
    return if (fontFamily != null) this else copy(fontFamily = default)
}


internal val LocalTypography = staticCompositionLocalOf { StudyCardsTypography() }