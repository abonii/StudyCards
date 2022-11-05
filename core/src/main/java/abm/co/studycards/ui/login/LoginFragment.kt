package abm.co.studycards.ui.login

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentLoginBinding
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import abm.co.studycards.util.navigate
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.google.android.gms.common.SignInButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseBindingFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIfUserLaunchedFirstTime()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        collectData()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun collectData() {
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.sharedFlow.collect { eventChannel ->
                when (eventChannel) {
                    LoginEventChannel.NavigateToForgotPassword -> {
                        navigateToForgotPassword()
                    }
                    LoginEventChannel.NavigateToMainActivity -> {
                        navigateToMainActivity()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun checkIfUserLaunchedFirstTime() {
//        val n = LoginFragmentDirections.actionLoginFragmentToFirstPageFragment()
//        navigate(n)
    }

    override fun initUI(savedInstanceState: Bundle?) {
        binding.run {
            viewmodel = viewModel
            (activity as LoginActivity).setToolbar(toolbar, R.drawable.ic_clear)
        }
    }

    private fun navigateToForgotPassword() {
        LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment().also {
            navigate(it)
        }
    }

    private fun navigateToMainActivity() {
        Intent(requireContext(), MainActivity::class.java).also {
            startActivity(it)
        }
        requireActivity().finish()
    }
}

@BindingAdapter("android:onClick")
fun bindSignInClick(button: SignInButton, method: () -> Unit) {
    button.setOnClickListener { method.invoke() }
}