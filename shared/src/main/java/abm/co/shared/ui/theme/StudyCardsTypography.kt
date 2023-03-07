package abm.co.shared.ui.theme

import abm.co.shared.R
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
object StudyCardsTypography {
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

    val wight400Size16LineHeight20: TextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.W400,
        lineHeight = 20.sp
    ).withDefaultFontFamily(defaultFontFamily)

    val wight400Size20LineHeight20: TextStyle = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.W400,
        lineHeight = 20.sp
    ).withDefaultFontFamily(defaultFontFamily)

    val wight600Size23LineHeight24: TextStyle = TextStyle(
        fontSize = 23.sp,
        fontWeight = FontWeight.W600,
        lineHeight = 24.sp
    ).withDefaultFontFamily(defaultFontFamily)

    val wight600Size14LineHeight18: TextStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.W600,
        lineHeight = 18.sp
    ).withDefaultFontFamily(defaultFontFamily)

    val wight400Size14LineHeight20: TextStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.W400,
        lineHeight = 20.sp
    ).withDefaultFontFamily(defaultFontFamily)

    val wight500Size14LineHeight20: TextStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.W500,
        lineHeight = 20.sp
    ).withDefaultFontFamily(defaultFontFamily)
}


/**
 * @return [this] if there is a [FontFamily] defined, otherwise copies [this] with [default] as
 * the [FontFamily].
 */
private fun TextStyle.withDefaultFontFamily(default: FontFamily): TextStyle {
    return if (fontFamily != null) this else copy(fontFamily = default)
}
