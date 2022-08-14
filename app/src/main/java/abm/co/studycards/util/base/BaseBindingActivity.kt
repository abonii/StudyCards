package abm.co.studycards.util.base

import abm.co.studycards.R
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseBindingActivity<B : ViewDataBinding>(@LayoutRes private val layoutResID: Int) :
    BaseActivity() {

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    //  Your view data binding
    private var _binding: B? = null
    val binding get() = _binding!!

    override fun onCreateUI(savedInstanceState: Bundle?) {
        //  Override Resources ID Layout
        setTheme(R.style.Theme_StudyCards)
        _binding = DataBindingUtil.setContentView(this, layoutResID)
        //  Initialize all widget in layout by ID
        initViews(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}