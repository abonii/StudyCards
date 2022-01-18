package abm.co.studycards.ui.sign

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentSignInBinding
import abm.co.studycards.util.base.BaseBindingFragment
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignInFragment : BaseBindingFragment<FragmentSignInBinding>(R.layout.fragment_sign_in) {

    companion object {
        private const val RC_SIGN_IN = 5005
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val viewModel: SignViewModel by viewModels()

    override fun initViews(savedInstanceState: Bundle?) {
        initFirebase()
        initUI()
        onClickListeners()
    }

    private fun initUI() {
        initGoogleSignBtn()
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

    private fun login() {
        showLoader()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        binding.error.isVisible =
            TextUtils.isEmpty(email) || TextUtils.isEmpty(password)

        when {
            TextUtils.isEmpty(email) -> {
                binding.error.text = "Email cannot be empty"
                hideLoader()
            }
            TextUtils.isEmpty(password) -> {
                binding.error.text = "Password cannot be empty"
                hideLoader()
            }
            else -> {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        checkIfEmailVerified()
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

    private fun checkIfEmailVerified() {
        val user = auth.currentUser
        if (user?.isEmailVerified == true) {
            updateUI(user)
        } else {
            toast(binding.root,"Please, verify your email address")
            FirebaseAuth.getInstance().signOut()
        }
    }

    private fun navigateToRegistration() {
        val n = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
        findNavController().navigate(n)
    }

    private fun initFirebase() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        auth = Firebase.auth

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        showLoader()
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        hideLoader()
        if (currentUser != null) {
            val i = Intent(requireContext(), MainActivity::class.java)
            startActivity(i)
            requireActivity().finish()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    updateUI(null)
                }
            }
    }
}