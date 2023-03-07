package abm.co.studycards.util.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment

abstract class BaseDialogFragment<B : ViewDataBinding>(@LayoutRes private val layoutResID: Int) :
    DialogFragment() {

    //  Your view data binding
    private var _binding: B? = null
    val binding get() = _binding!!

    //  Bind all widgets and start code
    protected abstract fun initUI(savedInstanceState: Bundle?)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutResID, container, false)
        _binding?.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    // Initialize all widget in layout by ID
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUI(savedInstanceState)
    }

    open fun toast(msg: Any, isDurationLong: Boolean = false) {
        context?.let {
            val duration = if (isDurationLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            when (msg) {
                is String ->
                    Toast.makeText(it, msg, duration)
                is Int ->
                    Toast.makeText(it, getString(msg), duration)
                else ->
                    Toast.makeText(it, msg.toString(), duration)
            }.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}