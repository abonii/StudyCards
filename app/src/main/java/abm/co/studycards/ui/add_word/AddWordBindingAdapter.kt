package abm.co.studycards.ui.add_word

import abm.co.studycards.R
import abm.co.studycards.data.model.AvailableLanguages
import abm.co.studycards.util.getProgressBarDrawable
import android.graphics.drawable.Animatable
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout

object AddWordBindingAdapter {
    @BindingAdapter("android:languageHint")
    @JvmStatic
    fun EditText.setLanguageHint(hint: String) {
        setHint(AvailableLanguages.getLanguageNameByCode(context, hint))
    }

    @BindingAdapter("android:languageText")
    @JvmStatic
    fun TextView.setLanguageText(hint: String) {
        text = (AvailableLanguages.getLanguageNameByCode(context, hint))
    }

    @BindingAdapter("android:isTranslating")
    @JvmStatic
    fun TextInputLayout.isTranslating(isTranslating: Boolean) {
        endIconDrawable = if (isTranslating) context?.getProgressBarDrawable().apply {
            (this as Animatable).start()
        } else ContextCompat.getDrawable(context, R.drawable.ic_translate)
    }
    @BindingAdapter("android:setImage")
    @JvmStatic
    fun ImageView.setImage(url:String?) {
        Glide.with(this)
            .load(url)
            .centerCrop()
            .placeholder(R.drawable.ic_image_search)
            .into(this)
    }
}