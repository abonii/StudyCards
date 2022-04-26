package abm.co.studycards.ui.forgot_password

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentForgotPasswordBinding
import abm.co.studycards.ui.login.LoginActivity
import abm.co.studycards.util.base.BaseBindingFragment
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordFragment :
    BaseBindingFragment<FragmentForgotPasswordBinding>(R.layout.fragment_forgot_password) {

    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        binding.run {
            viewmodel = viewModel
            (activity as LoginActivity).setToolbar(toolbar,R.drawable.ic_back)
        }
        lifecycleScope.launch {
            viewModel.toast.collect {
                toast(it)
            }
        }
    }

}