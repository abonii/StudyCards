package abm.co.studycards.util

import abm.co.studycards.R
import abm.co.studycards.helpers.LinkTouchMovementMethod
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.res.getDrawableOrThrow
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide

fun Number.px(): Int {
    return (this.toFloat() * Resources.getSystem().displayMetrics.density).toInt()
}

fun Number.dp(): Int {
    return (this.toInt() / Resources.getSystem().displayMetrics.density).toInt()
}

fun Number.sp(): Int {
    return (this.toInt() / Resources.getSystem().displayMetrics.density).toInt()
}

fun SearchView.setTypeFace(font: Typeface?) {
    val searchText = this.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
    searchText.apply {
        setTextColor(ContextCompat.getColor(this.context, R.color.textColor))
        gravity = Gravity.BOTTOM
        typeface = font
    }
}

fun ImageView.setImageWithGlide(url: String) {
    Glide.with(this.context)
        .load(url)
        .placeholder(R.drawable.ic_image)
        .into(this)
}

fun View.setNewWidthAndHeightForView(newWidth: Int, newHeight: Int) {
    this.apply {
        layoutParams = layoutParams.apply {
            width = newWidth
            height = newHeight
        }
    }
}

fun View.setNewHeightForView(newHeight: Int) {
    this.apply {
        layoutParams = layoutParams.apply {
            height = newHeight
        }
    }
}

fun Fragment.getMyColor(id: Int): Int {
    return resources.getColor(id, null)
}

fun Fragment.navigate(directions: NavDirections) {
    val controller = findNavController()
    val currentDestination =
        (controller.currentDestination as? FragmentNavigator.Destination)?.className
            ?: (controller.currentDestination as? DialogFragmentNavigator.Destination)?.className
    if (currentDestination == this.javaClass.name) {
        controller.navigate(directions)
    }
}

//
//fun TextView.makeClickable(
//    text: String,
//    listener: TranslatedWordAdapter.OnCheckBoxClicked,
//    isExample: Boolean
//) {
//    val spannableString = SpannableString(this.text)
//    var startIndexOfLink = -1
//    if (text.isNotEmpty()) {
//        val defaultColor =
//            if (isExample) getMyColor(R.color.second_color)
//            else getMyColor(R.color.text_color)
//        val selectedColor =
//            if (isExample) getMyColor(R.color.colorPrimary)
//            else getMyColor(R.color.colorPrimaryDark)
//        val clickableSpan = object : TouchableSpan(
//            defaultColor,
//            selectedColor
//        ) {
//            override fun onClick(view: View) {
//                if (isExample) {
//                    listener.onExampleSelected(text, myPressed)
//                } else {
//                    listener.onTranslationSelected(text, myPressed)
//                }
//                setMyPressed()
//            }
//
//        }
//        startIndexOfLink = this.text.toString().indexOf(text, startIndexOfLink + 1)
//        spannableString.setSpan(
//            clickableSpan, startIndexOfLink, startIndexOfLink + text.length,
//            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//    }
//    this.movementMethod =
//        LinkTouchMovementMethod()
//    this.setText(spannableString, TextView.BufferType.SPANNABLE)
//}


fun View.flipInCard(): AnimatorSet {
    val flipInAnimationSet =
        AnimatorInflater.loadAnimator(
            context,
            R.animator.flip_in
        ) as AnimatorSet
    flipInAnimationSet.setTarget(this)
    flipInAnimationSet.start()
    return flipInAnimationSet
}

fun View.flipOutCard(): AnimatorSet {
    val flipInAnimationSet =
        AnimatorInflater.loadAnimator(
            context,
            R.animator.flip_out
        ) as AnimatorSet
    flipInAnimationSet.setTarget(this)
    flipInAnimationSet.start()
    return flipInAnimationSet
}

fun View.changeBackgroundChangesAndFlip(color: Int): ObjectAnimator {
    val backgroundColorAnimator = ObjectAnimator.ofObject(
        this,
        "backgroundColor",
        ArgbEvaluator(),
        Color.TRANSPARENT,
        color
    )
    backgroundColorAnimator.duration = 300
    backgroundColorAnimator.start()
    return backgroundColorAnimator
}

fun Context.getProgressBarDrawable(): Drawable {
    val value = TypedValue()
    theme.resolveAttribute(android.R.attr.progressBarStyleSmall, value, false)
    val progressBarStyle = value.data
    val attributes = intArrayOf(android.R.attr.indeterminateDrawable)
    val array = obtainStyledAttributes(progressBarStyle, attributes)
    val drawable = array.getDrawableOrThrow(0)
    array.recycle()

    return drawable
}