package abm.co.studycards.data.model

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

data class Language(@StringRes val languageResCode: Int, val code: String, @DrawableRes val imageFromDrawable: Int) {

    fun getLanguageName(@NonNull context: Context):String{
        return context.getString(languageResCode)
    }

    fun getDrawable(@NonNull context: Context): Drawable? {
        return ContextCompat.getDrawable(context, imageFromDrawable)
    }

}