package abm.co.feature.userattributes.lanugage

import abm.co.domain.model.Language
import abm.co.feature.R
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class LanguageUI(
    val code: String,
    @StringRes val languageNameResCode: Int,
    @DrawableRes val flagFromDrawable: Int
)

fun LanguageUI.toDomain() = Language(
    code = code,
    languageNameResCode = languageNameResCode,
    iconFromDrawable = flagFromDrawable
)

fun Language.toUI() = LanguageUI(
    code = code,
    languageNameResCode = languageNameResCode,
    flagFromDrawable = iconFromDrawable
)


val defaultLanguages = listOf(
    LanguageUI(
        code = "ar",
        languageNameResCode = R.string.Arabic,
        flagFromDrawable = R.drawable.flag_saudi_arabia
    ), LanguageUI(
        code = "bn",
        languageNameResCode = R.string.Bengali,
        flagFromDrawable = R.drawable.flag_bangladesh
    ), LanguageUI(
        code = "zh",
        languageNameResCode = R.string.Chinese,
        flagFromDrawable = R.drawable.flag_china
    ), LanguageUI(
        code = "en",
        languageNameResCode = R.string.English,
        flagFromDrawable = R.drawable.flag_united_kingdom
    ), LanguageUI(
        code = "fr",
        languageNameResCode = R.string.French,
        flagFromDrawable = R.drawable.flag_france
    ), LanguageUI(
        code = "de",
        languageNameResCode = R.string.German,
        flagFromDrawable = R.drawable.flag_germany
    ), LanguageUI(
        code = "hi",
        languageNameResCode = R.string.Hindi,
        flagFromDrawable = R.drawable.flag_india
    ), LanguageUI(
        code = "id",
        languageNameResCode = R.string.Indonesian,
        flagFromDrawable = R.drawable.flag_indonesia
    ), LanguageUI(
        code = "it",
        languageNameResCode = R.string.Italian,
        flagFromDrawable = R.drawable.flag_italy
    ), LanguageUI(
        code = "ja",
        languageNameResCode = R.string.Japanese,
        flagFromDrawable = R.drawable.flag_japan
    ), LanguageUI(
        code = "kk",
        languageNameResCode = R.string.Kazakh,
        flagFromDrawable = R.drawable.flag_kazakhstan
    ), LanguageUI(
        code = "ko",
        languageNameResCode = R.string.Korean,
        flagFromDrawable = R.drawable.flag_south_korea
    ), LanguageUI(
        code = "pt",
        languageNameResCode = R.string.Portuguese,
        flagFromDrawable = R.drawable.flag_portugal
    ), LanguageUI(
        code = "es",
        languageNameResCode = R.string.Spanish,
        flagFromDrawable = R.drawable.flag_spain
    ), LanguageUI(
        code = "tr",
        languageNameResCode = R.string.Turkish,
        flagFromDrawable = R.drawable.flag_turkey
    ), LanguageUI(
        code = "uk",
        languageNameResCode = R.string.Ukrainian,
        flagFromDrawable = R.drawable.flag_ukraine
    ), LanguageUI(
        code = "ru",
        languageNameResCode = R.string.Russian,
        flagFromDrawable = R.drawable.flag_russia
    )
)

fun List<LanguageUI>.filterByCodes(codes: List<String>) = filter { lang -> codes.any { it == lang.code } }

fun List<LanguageUI>.findByCode(code: String) = find { lang -> code == lang.code }
