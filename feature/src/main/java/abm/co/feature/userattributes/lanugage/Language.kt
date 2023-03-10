package abm.co.feature.userattributes.lanugage

import abm.co.feature.R
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class Language(
    val code: String,
    @StringRes val languageResCode: Int,
    @DrawableRes val imageFromDrawable: Int
)

val availableLanguages = listOf(
    Language(
        code = "ar",
        languageResCode = R.string.Arabic,
        imageFromDrawable = R.drawable.flag_saudi_arabia
    ), Language(
        code = "bn",
        languageResCode = R.string.Bengali,
        imageFromDrawable = R.drawable.flag_bangladesh
    ), Language(
        code = "zh", languageResCode = R.string.Chinese, imageFromDrawable = R.drawable.flag_china
    ), Language(
        code = "en",
        languageResCode = R.string.English,
        imageFromDrawable = R.drawable.flag_united_kingdom
    ), Language(
        code = "fr", languageResCode = R.string.French, imageFromDrawable = R.drawable.flag_france
    ), Language(
        code = "de", languageResCode = R.string.German, imageFromDrawable = R.drawable.flag_germany
    ), Language(
        code = "hi", languageResCode = R.string.Hindi, imageFromDrawable = R.drawable.flag_india
    ), Language(
        code = "id",
        languageResCode = R.string.Indonesian,
        imageFromDrawable = R.drawable.flag_indonesia
    ), Language(
        code = "it", languageResCode = R.string.Italian, imageFromDrawable = R.drawable.flag_italy
    ), Language(
        code = "ja", languageResCode = R.string.Japanese, imageFromDrawable = R.drawable.flag_japan
    ), Language(
        code = "kk",
        languageResCode = R.string.Kazakh,
        imageFromDrawable = R.drawable.flag_kazakhstan
    ), Language(
        code = "ko",
        languageResCode = R.string.Korean,
        imageFromDrawable = R.drawable.flag_south_korea
    ), Language(
        code = "pt",
        languageResCode = R.string.Portuguese,
        imageFromDrawable = R.drawable.flag_portugal
    ), Language(
        code = "es", languageResCode = R.string.Spanish, imageFromDrawable = R.drawable.flag_spain
    ), Language(
        code = "tr", languageResCode = R.string.Turkish, imageFromDrawable = R.drawable.flag_turkey
    ), Language(
        code = "uk",
        languageResCode = R.string.Ukrainian,
        imageFromDrawable = R.drawable.flag_ukraine
    ), Language(
        code = "ru", languageResCode = R.string.Russian, imageFromDrawable = R.drawable.flag_russia
    )
)

fun List<Language>.f(codes: List<String>) = filter { lang -> codes.any { it == lang.code } }
