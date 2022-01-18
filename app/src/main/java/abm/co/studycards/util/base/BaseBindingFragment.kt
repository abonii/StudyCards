package abm.co.studycards.util.base

import abm.co.studycards.MainActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseBindingFragment<B : ViewDataBinding>(@LayoutRes private val layoutResID: Int) :
    BaseFragment() {

    //  Your view data binding
    private var _binding: B? = null
    val binding get() = _binding!!

    //  Bind all widgets and start code
    protected abstract fun initViews(savedInstanceState: Bundle?)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutResID, container, false)
        return binding.root
    }

    // Initialize all widget in layout by ID
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun onBackPressed() {
        if (activity is MainActivity) {
            (activity as MainActivity).onBackPressed()
        }
    }
}