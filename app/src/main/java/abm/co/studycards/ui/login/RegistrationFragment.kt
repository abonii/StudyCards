package abm.co.studycards.ui.login

import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentSignUpBinding
import abm.co.studycards.util.base.BaseBindingFragment
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationFragment : BaseBindingFragment<FragmentSignUpBinding>(R.layout.fragment_sign_up) {

    private val viewModel: LoginViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        onClickListeners()
    }

    private fun sendVerificationEmail() {
        viewModel.firebaseAuthInstance.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModel.firebaseAuthInstance.signOut()
                    toast(R.string.we_send_verification)
                    navigateToLogin()
                } else {
                    toast(R.string.we_couldnt_send_verification)
                }
            }
    }

    private fun onClickListeners() {
        binding.login.setOnClickListener {
            navigateToLogin()
        }
        binding.register.setOnClickListener {
            register()
        }
    }

    private fun navigateToLogin() {
        val n = RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment()
        findNavController().navigate(n)
    }

    private fun register() {
        showLoader()
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()
        val verifyPassword = binding.verifyPassword.text.toString().trim()
        binding.error.isVisible =
            TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(
                verifyPassword
            ) || !TextUtils.equals(verifyPassword, password)
        when {
            TextUtils.isEmpty(email) -> {
                binding.error.text = getStr(R.string.email_empty)
                hideLoader()
            }
            TextUtils.isEmpty(password) -> {
                binding.error.text = getStr(R.string.password_empty)
                hideLoader()
            }
            TextUtils.isEmpty(verifyPassword) -> {
                binding.error.text = getStr(R.string.password_empty)
                hideLoader()
            }
            verifyPassword.length < 5 -> {
                binding.error.text = getStr(R.string.password_empty)
                hideLoader()
            }
            !TextUtils.equals(verifyPassword, password) -> {
                binding.error.text = getString(R.string.passwords_not_same)
                hideLoader()
            }
            else -> {
                viewModel.firebaseAuthInstance.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            sendVerificationEmail()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "${it.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        hideLoader()
                    }
            }
        }
    }
}