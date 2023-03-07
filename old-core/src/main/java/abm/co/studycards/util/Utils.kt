package abm.co.studycards.util

import abm.co.studycards.R
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.getDrawableOrThrow
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import java.util.*


fun Number.px(): Int {
    return (this.toFloat() * Resources.getSystem().displayMetrics.density).toInt()
}

fun Number.dp(): Int {
    return (this.toInt() / Resources.getSystem().displayMetrics.density).toInt()
}

fun View.getMyColor(id: Int): Int {
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

fun Fragment.navigate(id: Int, bundle: Bundle? = null) {
    val controller = findNavController()
    val currentDestination =
        (controller.currentDestination as? FragmentNavigator.Destination)?.className
            ?: (controller.currentDestination as? DialogFragmentNavigator.Destination)?.className
    if (currentDestination == this.javaClass.name) {
        controller.navigate(id, bundle)
    }
}

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

fun CardView.changeBackgroundChangesAndFlip(
    color: Int,
    currentColor: Int = this.getMyColor(R.color.background),
    duration: Long = 400
): ObjectAnimator {
    val backgroundColorAnimator = ObjectAnimator.ofObject(
        this,
        "cardBackgroundColor",
        ArgbEvaluator(),
        currentColor,
        color
    )
    backgroundColorAnimator.duration = duration
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

@Suppress("DEPRECATION")
fun String?.fromHtml(): CharSequence {
    return when {
        this == null -> {
            SpannableString("")
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
            Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
        }
        else -> {
            Html.fromHtml(this)
        }
    }
}

@Suppress("DEPRECATION")
fun TextView.leftAndRightDrawable(
    @DrawableRes left: Int = 0,
    @DrawableRes right: Int = R.drawable.ic_arrow_down,
    @DimenRes sizeRes: Int
) {
    val drawableL = ContextCompat.getDrawable(context, left)
    val drawableR = ContextCompat.getDrawable(context, right)
    val size = resources.getDimensionPixelSize(sizeRes)
    drawableL?.setBounds(0, -3, size, size)
    drawableR?.setBounds(0, 0, size, size)
    val colorInt = ContextCompat.getColor(context, R.color.textColor)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        drawableR?.colorFilter = BlendModeColorFilter(colorInt, BlendMode.SRC_ATOP)
    } else {
        drawableR?.setColorFilter(colorInt, PorterDuff.Mode.SRC_ATOP)
    }
    this.setCompoundDrawables(drawableL, null, drawableR, null)
}

fun Calendar.toStartOfTheDay(): Calendar {
    return this.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }
}

fun Calendar.toDay(dayCount: Int): Calendar {
    return this.apply {
        add(Calendar.DAY_OF_MONTH, dayCount)
    }
}

/**
 * Launches a new coroutine and repeats `block` every time the Fragment's viewLifecycleOwner
 * is in and out of `minActiveState` lifecycle state.
 */
inline fun Fragment.launchAndRepeatWithViewLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
            block()
        }
    }
}

fun String.stripAccents(): String {
    return StringUtils.stripAccents(this)
}
