package abm.co.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Language(
    val code: String,
    @StringRes val languageResCode: Int,
    @DrawableRes val imageFromDrawable: Int
)
