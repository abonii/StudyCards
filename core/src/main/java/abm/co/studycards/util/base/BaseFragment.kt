package abm.co.studycards.util.base

import abm.co.studycards.R
import abm.co.studycards.util.Constants.TAG
import android.app.AlertDialog
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar


abstract class BaseFragment : Fragment(), IResourcesIDListener {

    private var dialogForLoader: Dialog? = null

    /*  Modal windows  */
    open fun snackbar(view: View, msg: Any, isDurationLong: Boolean = false) {
        context?.let {
            val duration = if (isDurationLong) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT
            val snackbar = when (msg) {
                is String ->
                    Snackbar.make(view, msg, duration)
                is Int ->
                    Snackbar.make(view, getStr(msg), duration)
                else ->
                    Snackbar.make(view, msg.toString(), duration)
            }
            val snackView = snackbar.view
            val params = snackView.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            val dimen = getDimen(R.dimen.default_10dp).toInt()
            params.setMargins(
                params.leftMargin + dimen,
                params.topMargin + dimen / 2,
                params.rightMargin + dimen,
                params.bottomMargin
            )
            val tv =
                snackView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            tv.setTextColor(getClr(R.color.white))
            snackView.layoutParams = params
            snackView.background = getImg(R.drawable.round_corner)
            snackView.backgroundTintList = ColorStateList.valueOf(getClr(R.color.colorPrimary))
            snackbar.show()
        }
    }

    open fun toast(msg: Any, isDurationLong: Boolean = false) {
        context?.let {
            val duration = if (isDurationLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            when (msg) {
                is String ->
                    Toast.makeText(it, msg, duration)
                is Int ->
                    Toast.makeText(it, getStr(msg), duration)
                else ->
                    Toast.makeText(it, msg.toString(), duration)
            }.show()
        }
    }

    fun showILog(msg: Any, name: String = TAG) {
        Log.i(name, msg.toString())
    }


    open fun hideLoader() {
        dialogForLoader?.dismiss()
        killDialog()
    }

    open fun showLoader(cancelable: Boolean = false) {
        val progressBar = ProgressBar(requireContext())
        progressBar.indeterminateDrawable = getImg(R.drawable.progress_anim)
        progressBar.background = getImg(R.drawable.custom_progress_background)
        dialogForLoader = AlertDialog.Builder(context, R.style.WrapContentDialog).apply {
            setView(progressBar)
            setCancelable(cancelable)
            create()

        }.show()
        dialogForLoader?.setOnKeyListener { dialog, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK &&
                keyEvent.action == KeyEvent.ACTION_UP &&
                !keyEvent.isCanceled
            ) {
                dialog.cancel()
                findNavController().popBackStack()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    private fun killDialog() {
        dialogForLoader = null
    }

    /*  Resources ID getters  */

    /*
     *  If your app supported more one language,
     *  you can add your locale
     *  example -> yourResources.getString(id);
     */
    override fun getStr(@StringRes id: Int): String = getString(id)

    /*
     * Get drawable (png, jpg, svg, ....) by ID
     */
    @Nullable
    override fun getImg(@DrawableRes id: Int): Drawable? =
        ContextCompat.getDrawable(requireActivity(), id)

    /*
     * Get color by ID
     */
    @ColorInt
    override fun getClr(@ColorRes id: Int): Int = ContextCompat.getColor(requireActivity(), id)

    /*
    * Get dimension by ID
    */
    override fun getDimen(@DimenRes id: Int): Float = resources.getDimension(id)
}