package abm.co.feature.userattributes.lanugage

import abm.co.domain.model.Language
import abm.co.feature.R
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class LanguageUI(
    val code: String,
    @StringRes val languageResCode: Int,
    @DrawableRes val imageFromDrawable: Int
)

fun LanguageUI.toDomain() = Language(
    code = code,
    languageResCode = languageResCode,
    imageFromDrawable = imageFromDrawable
)

fun Language.toUI() = LanguageUI(
    code = code,
    languageResCode = languageResCode,
    imageFromDrawable = imageFromDrawable
)


val defaultLanguages = listOf(
    LanguageUI(
        code = "ar",
        languageResCode = R.string.Arabic,
        imageFromDrawable = R.drawable.flag_saudi_arabia
    ), LanguageUI(
        code = "bn",
        languageResCode = R.string.Bengali,
        imageFromDrawable = R.drawable.flag_bangladesh
    ), LanguageUI(
        code = "zh",
        languageResCode = R.string.Chinese,
        imageFromDrawable = R.drawable.flag_china
    ), LanguageUI(
        code = "en",
        languageResCode = R.string.English,
        imageFromDrawable = R.drawable.flag_united_kingdom
    ), LanguageUI(
        code = "fr",
        languageResCode = R.string.French,
        imageFromDrawable = R.drawable.flag_france
    ), LanguageUI(
        code = "de",
        languageResCode = R.string.German,
        imageFromDrawable = R.drawable.flag_germany
    ), LanguageUI(
        code = "hi",
        languageResCode = R.string.Hindi,
        imageFromDrawable = R.drawable.flag_india
    ), LanguageUI(
        code = "id",
        languageResCode = R.string.Indonesian,
        imageFromDrawable = R.drawable.flag_indonesia
    ), LanguageUI(
        code = "it",
        languageResCode = R.string.Italian,
        imageFromDrawable = R.drawable.flag_italy
    ), LanguageUI(
        code = "ja",
        languageResCode = R.string.Japanese,
        imageFromDrawable = R.drawable.flag_japan
    ), LanguageUI(
        code = "kk",
        languageResCode = R.string.Kazakh,
        imageFromDrawable = R.drawable.flag_kazakhstan
    ), LanguageUI(
        code = "ko",
        languageResCode = R.string.Korean,
        imageFromDrawable = R.drawable.flag_south_korea
    ), LanguageUI(
        code = "pt",
        languageResCode = R.string.Portuguese,
        imageFromDrawable = R.drawable.flag_portugal
    ), LanguageUI(
        code = "es",
        languageResCode = R.string.Spanish,
        imageFromDrawable = R.drawable.flag_spain
    ), LanguageUI(
        code = "tr",
        languageResCode = R.string.Turkish,
        imageFromDrawable = R.drawable.flag_turkey
    ), LanguageUI(
        code = "uk",
        languageResCode = R.string.Ukrainian,
        imageFromDrawable = R.drawable.flag_ukraine
    ), LanguageUI(
        code = "ru",
        languageResCode = R.string.Russian,
        imageFromDrawable = R.drawable.flag_russia
    )
)

fun List<LanguageUI>.f(codes: List<String>) = filter { lang -> codes.any { it == lang.code } }
