package abm.co.studycards.util

import abm.co.studycards.R
import abm.co.studycards.util.Constants.PROFILE_DEF_IMAGE
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout


object GeneralBindingAdapters {

    @JvmStatic
    @BindingAdapter("app:errorText")
    fun setErrorMessage(view: TextInputLayout, errorMessage: String?) {
        view.error = errorMessage
    }

    @JvmStatic
    @BindingAdapter("app:imageWithGlide")
    fun ImageView.setImageWithGlide(url: String?) {
        if (url?.isNotBlank() == true) {
            Glide.with(context)
                .load(url)
                .timeout(4000)
                .error(R.color.third_color)
                .into(this)
        } else {
            Glide.with(context)
                .load("https://wallpaperaccess.com/full/3222084.jpg")
                .timeout(4000)
                .into(this)
        }
    }

    @JvmStatic
    @BindingAdapter("app:imageWithGlideAndErrorImage")
    fun ImageView.setImageWithGlideAndErrorImage(url: String?) {
        if (url?.isNotBlank() == true) {
            Glide.with(context)
                .load(url)
                .error(R.drawable.ic_image)
                .into(this)
        }
    }

    @JvmStatic
    @BindingAdapter("app:imageForProfileWithGlide")
    fun ImageView.setProfileImageWithGlide(url: Uri?) {
        if (url != null) Glide.with(context).load(url).error(PROFILE_DEF_IMAGE).into(this)
        else Glide.with(context).load(PROFILE_DEF_IMAGE).into(this)
    }
}