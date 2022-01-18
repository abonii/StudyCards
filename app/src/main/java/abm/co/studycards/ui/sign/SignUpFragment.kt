package abm.co.studycards.ui.sign

import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentSignUpBinding
import abm.co.studycards.util.base.BaseBindingFragment
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseBindingFragment<FragmentSignUpBinding>(R.layout.fragment_sign_up) {

    private val viewModel: SignViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun initViews(savedInstanceState: Bundle?) {
        initFirebase()
        onClickListeners()
    }

    private fun sendVerificationEmail() {
        val user = FirebaseAuth.getInstance().currentUser
        user!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "We send verification to your email, please check your email",
                        Toast.LENGTH_SHORT
                    ).show()
                    FirebaseAuth.getInstance().signOut()
                    navigateToLogin()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "We cannot send verification to this email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun initFirebase() {
        auth = FirebaseAuth.getInstance()
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
        if (parentFragmentManager.backStackEntryCount > 0) {
            findNavController().popBackStack()
        } else {
            val n = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
            findNavController().navigate(n)
        }
    }

    @SuppressLint("SetTextI18n")
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
                binding.error.text = "Email cannot be empty"
                hideLoader()
            }
            TextUtils.isEmpty(password) -> {
                binding.error.text = "Password cannot be empty"
                hideLoader()
            }
            TextUtils.isEmpty(verifyPassword) -> {
                binding.error.text = "Password2 cannot be empty"
                hideLoader()
            }
            !TextUtils.equals(verifyPassword, password) -> {
                binding.error.text = "Passwords are not same"
                hideLoader()
            }
            else -> {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        sendVerificationEmail()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Registration Error:${it.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    hideLoader()
                }
            }
        }
    }
}