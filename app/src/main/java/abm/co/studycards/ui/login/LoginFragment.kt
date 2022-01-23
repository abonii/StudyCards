package abm.co.studycards.ui.login

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentSignInBinding
import abm.co.studycards.util.base.BaseBindingFragment
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
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
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                }
            }
        }

    override fun initUI(savedInstanceState: Bundle?) {
        initGoogleSignBtn()
        onClickListeners()
    }


    private fun setClickable(view: View?, isIt: Boolean) {
        if (view != null) {
            view.isClickable = isIt
            if (view is EditText)
                view.isCursorVisible = isIt
            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    setClickable(view.getChildAt(i), isIt)
                }
            }
        }
    }

    private fun initGoogleSignBtn() {
        binding.googleSignIn.getChildAt(0)?.let {
            (it as TextView).text = getString(R.string.login)
            val smaller = it.paddingLeft.coerceAtMost(it.paddingRight)
            it.setPadding(smaller, it.paddingTop, smaller, it.paddingBottom)
        }
    }

    private fun onClickListeners() {
        binding.googleSignIn.setOnClickListener {
            signIn()
        }
        binding.register.setOnClickListener {
            navigateToRegistration()
        }
        binding.loginBtn.setOnClickListener {
            login()
        }
    }

    private fun signIn() {
        val intent = Intent(viewModel.googleSignInClient.signInIntent)
        launchSomeActivity.launch(intent)
    }

    private fun login() {
        showProgressBar()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        binding.error.isVisible =
            TextUtils.isEmpty(email) || TextUtils.isEmpty(password)

        when {
            TextUtils.isEmpty(email) -> {
                binding.error.text = getString(R.string.email_empty)
                hideProgressBar()
            }
            TextUtils.isEmpty(password) -> {
                binding.error.text = getString(R.string.password_empty)
                hideProgressBar()
            }
            else -> {
                viewModel.firebaseAuthInstance.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            checkIfEmailVerified()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "${it.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        hideProgressBar()
                    }
            }
        }
    }

    private fun checkIfEmailVerified() {
        val user = viewModel.firebaseAuthInstance.currentUser
        if (user?.isEmailVerified == true) {
            updateUI(user)
        } else {
            toast(getString(R.string.email_not_verified))
            FirebaseAuth.getInstance().signOut()
        }
    }

    private fun navigateToRegistration() {
        val n = LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
        findNavController().navigate(n)
    }

    private fun showProgressBar() {
        binding.progress.visibility = View.VISIBLE
        binding.container.animate().alpha(0.5f)
        setClickable(binding.container, false)

    }

    private fun hideProgressBar() {
        binding.progress.visibility = View.GONE
        binding.container.animate().alpha(1f)
        setClickable(binding.container, true)
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = viewModel.firebaseAuthInstance.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        hideProgressBar()
        if (currentUser != null) {
            val i = Intent(requireContext(), MainActivity::class.java)
            startActivity(i)
            requireActivity().finish()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        showProgressBar()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        viewModel.firebaseAuthInstance.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = viewModel.firebaseAuthInstance.currentUser
                    updateUI(user)
                } else {
                    updateUI(null)
                }
            }
    }
}