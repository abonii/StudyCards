package abm.co.data.model.user

import abm.co.domain.model.Language
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.StringRes

@Keep
data class LanguageDTO(
    val code: String,
    @StringRes val languageNameResCode: Int,
    @DrawableRes val iconFromDrawable: Int
)

fun LanguageDTO.toDomain() = Language(
    code = code,
    languageNameResCode = languageNameResCode,
    iconFromDrawable = iconFromDrawable
)

fun Language.toDTO() = LanguageDTO(
    code = code,
    languageNameResCode = languageNameResCode,
    iconFromDrawable = iconFromDrawable
)
