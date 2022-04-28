package abm.co.studycards.ui.registration

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentRegistrationBinding
import abm.co.studycards.ui.login.LoginActivity
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegistrationFragment :
    BaseBindingFragment<FragmentRegistrationBinding>(R.layout.fragment_registration) {

    private val viewModel: RegistrationViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        binding.run {
            viewmodel = viewModel
            registrationFragment = this@RegistrationFragment
        }
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.sharedFlow.collect { eventChannel ->
                when (eventChannel) {
                    RegistrationEventChannel.NavigateToMainActivity -> {
                        navigateToMainActivity()
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

    override fun onResume() {
        super.onResume()
        (activity as LoginActivity).setToolbar(binding.toolbar, R.drawable.ic_clear)
    }

    private fun navigateToMainActivity() {
        Intent(requireContext(), MainActivity::class.java).also {
            startActivity(it)
        }
        requireActivity().finish()
    }


}