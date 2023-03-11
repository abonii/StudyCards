package abm.co.data.model.user

import abm.co.domain.model.Language
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class LanguageDTO(
    val code: String,
    @StringRes val languageResCode: Int,
    @DrawableRes val imageFromDrawable: Int
)

fun LanguageDTO.toDomain() = Language(
    code = code,
    languageResCode = languageResCode,
    imageFromDrawable = imageFromDrawable
)

fun Language.toDTO() = LanguageDTO(
    code = code,
    languageResCode = languageResCode,
    imageFromDrawable = imageFromDrawable
)
