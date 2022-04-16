package abm.co.studycards.ui.forgot_password

import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentForgotPasswordBinding
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.setClickableForAllChildren
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.SignInButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordFragment :
    BaseBindingFragment<FragmentForgotPasswordBinding>(R.layout.fragment_forgot_password) {

    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        binding.run {
            viewmodel = viewModel
        }
        lifecycleScope.launch {
            viewModel.toast.collect {
                toast(it)
            }
        }
    }

}