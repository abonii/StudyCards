package abm.co.studycards.util.base

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
    var binding: B? = null

    override fun onCreateUI(savedInstanceState: Bundle?) {
        //  Override Resources ID Layout
        binding = DataBindingUtil.setContentView(this, layoutResID)
        //  Initialize all widget in layout by ID
        initViews(savedInstanceState)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (binding != null) binding = null
    }
}