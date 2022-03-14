package abm.co.studycards.ui.registration

import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentSignUpBinding
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import abm.co.studycards.util.navigate
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegistrationFragment : BaseBindingFragment<FragmentSignUpBinding>(R.layout.fragment_sign_up) {

    private val viewModel: RegistrationViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        binding.run {
            viewmodel = viewModel
            registrationFragment = this@RegistrationFragment
        }
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.sharedFlow.collect { eventChannel ->
                when (eventChannel) {
                    RegistrationEventChannel.NavigateToLogin -> {
                        navigateToLogin()
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.toast.collect {
                toast(it)
            }
        }
    }

    private fun navigateToLogin() {
        RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment().also {
            navigate(it)
        }
    }


}