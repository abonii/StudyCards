package abm.co.studycards.util

import abm.co.studycards.R
import android.net.Uri
import android.widget.ImageView
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
                .error(R.drawable.ic_image)
                .into(this)
        }
    }

    @JvmStatic
    @BindingAdapter("app:imageWithGlide")
    fun ImageView.setImageWithGlide(url: Uri?) {
        if (url != null) {
            Glide.with(context)
                .load(url)
                .into(this)
        }
    }
}