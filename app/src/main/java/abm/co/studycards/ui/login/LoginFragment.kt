package abm.co.studycards.ui.login

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentSignInBinding
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import abm.co.studycards.util.navigate
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseBindingFragment<FragmentSignInBinding>(R.layout.fragment_sign_in) {

    private val viewModel: LoginViewModel by viewModels()

    private var launchSomeActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    viewModel.firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    toast("error " + e.message)
                }
            }
        }

    override fun initUI(savedInstanceState: Bundle?) {
        initGoogleSignBtn()
        binding.run {
            viewmodel = viewModel
            emailEditText.addTextChangedListener {
                viewModel.email = it.toString().trim()
            }
            passwordEditText.addTextChangedListener {
                viewModel.password = it.toString().trim()
            }
        }
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.sharedFlow.collect { eventChannel ->
                when (eventChannel) {
                    LoginEventChannel.NavigateToForgotPassword -> {
                        navigateToForgotPassword()
                    }
                    LoginEventChannel.NavigateToMainActivity -> {
                        navigateToMainActivity()
                    }
                    LoginEventChannel.NavigateToRegistration -> {
                        navigateToRegistration()
                    }
                    is LoginEventChannel.LoginViaGoogle -> {
                        launchSomeActivity.launch(eventChannel.intent)
                    }
                }
            }
        }
    }

    private fun navigateToForgotPassword() {
        LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment().also {
            navigate(it)
        }
    }

    private fun navigateToRegistration() {
        LoginFragmentDirections.actionLoginFragmentToSignUpFragment().also {
            navigate(it)
        }
    }

    private fun navigateToMainActivity() {
        Intent(requireContext(), MainActivity::class.java).also {
            startActivity(it)
        }
        requireActivity().finish()
    }

    private fun initGoogleSignBtn() = binding.run {
        googleSignIn.getChildAt(0)?.let {
            (it as TextView).text = getString(R.string.google_sign)
            val smaller = it.paddingLeft.coerceAtMost(it.paddingRight)
            it.setPadding(smaller, it.paddingTop, smaller, it.paddingBottom)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkUserExistence()
    }

//    private fun firebaseAuthWithGoogle(idToken: String) {
//        showProgressBar()
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        viewModel.firebaseAuthInstance.signInWithCredential(credential)
//            .addOnCompleteListener(requireActivity()) { task ->
//                if (task.isSuccessful) {
//                    viewModel.checkUserExistence()
//                } else {
//                    viewModel.checkUserExistence(null)
//                }
//            }
//    }
}

@BindingAdapter("android:onClick")
fun bindSignInClick(button: SignInButton, method: () -> Unit) {
    button.setOnClickListener { method.invoke() }
}