package abm.co.studycards.ui.forgot_password

import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentForgotPasswordBinding
import abm.co.studycards.util.base.BaseBindingFragment
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
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