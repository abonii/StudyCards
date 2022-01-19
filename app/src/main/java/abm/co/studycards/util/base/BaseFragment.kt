package abm.co.studycards.util.base

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar


abstract class BaseFragment : Fragment(), IResourcesIDListener {

    private var dialogForLoader: Dialog? = null

    /*  Modal windows  */
    open fun snackbar(view: View, msg: Any, isDurationLong: Boolean = false) {
        context?.let {
            val duration = if (isDurationLong) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT
            when (msg) {
                is String ->
                    Snackbar.make(view, msg, duration)
                is Int ->
                    Snackbar.make(view, getStr(msg), duration)
                else ->
                    Snackbar.make(view, msg.toString(), duration)
            }.show()
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

    open fun showLog(msg: Any,name: String = "Voca") {
        Log.i(name, msg.toString())
    }

    fun showLoader() {
    }

    fun hideLoader() {
        dialogForLoader?.dismiss()
        killDialog()
    }

    private fun killDialog() {
        if (dialogForLoader != null)
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
     * Concat all your text, strings and resources,
     * to one String
     */
    override fun concatStr(text: String): String = text

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
}