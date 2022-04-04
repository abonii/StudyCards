package abm.co.studycards.data.model

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import kotlinx.parcelize.Parcelize

@Parcelize
data class Language(
    @StringRes val languageResCode: Int,
    val code: String,
    @DrawableRes val imageFromDrawable: Int
) : Parcelable {

    fun getLanguageName(@NonNull context: Context): String {
        return context.getString(languageResCode)
    }

    fun getDrawable(@NonNull context: Context): Drawable? {
        return ContextCompat.getDrawable(context, imageFromDrawable)
    }

}