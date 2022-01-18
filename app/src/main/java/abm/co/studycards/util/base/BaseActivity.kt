package abm.co.studycards.util.base

import abm.co.studycards.common.LocaleHelper
import abm.co.studycards.data.pref.Prefs
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

abstract class BaseActivity : AppCompatActivity(), IResourcesIDListener {

    private var dialogForKMFLoader: Dialog? = null
    private var isShowMsg = false

    //  Initialize all widget in layout
    protected abstract fun initViews(savedInstanceState: Bundle?)
    protected abstract fun onCreateUI(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateUI(savedInstanceState)
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun attachBaseContext(newBase: Context) {
        val lang = getLang(newBase)
        val context = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(context)
    }

    private fun getLang(context: Context): String {
        val sf = context.getSharedPreferences(Prefs.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        return sf.getString(Prefs.SELECTED_APP_LANGUAGE, "en") ?: "en"
    }


    /*  Modal windows  */
    open fun toast(context: Context?, msg: Any, isDuration: Boolean = false) {
        context?.let {
            val duration = if (isDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            when (msg) {
                is String ->
                    Toast.makeText(context, msg, duration).show()
                is Int ->
                    Toast.makeText(context, msg, duration).show()
            }
        }
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
    override fun getImg(@DrawableRes id: Int): Drawable? = ContextCompat.getDrawable(this, id)

    /*
     * Get color by ID
     */
    @ColorInt
    override fun getClr(@ColorRes id: Int): Int = ContextCompat.getColor(this, id)
}