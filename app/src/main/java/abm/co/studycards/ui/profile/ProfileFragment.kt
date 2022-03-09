package abm.co.studycards.ui.profile

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.common.LocaleHelper
import abm.co.studycards.data.model.AvailableLanguages
import abm.co.studycards.data.model.Language
import abm.co.studycards.databinding.FragmentProfileBinding
import abm.co.studycards.ui.login.LoginActivity
import abm.co.studycards.ui.settings.LanguageOfTheAppAdapter
import abm.co.studycards.ui.settings.LanguageOfTheAppDialogFragment
import abm.co.studycards.util.Constants.SHOULD_I_OPEN_PROFILE_FRAGMENT
import abm.co.studycards.util.base.BaseBindingFragment
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : BaseBindingFragment<FragmentProfileBinding>(R.layout.fragment_profile),
    LanguageOfTheAppAdapter.OnClick {

    private val viewModel: ProfileViewModel by viewModels()

    private var launchSomeActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                    showLog("token " + account.idToken!!)
                } catch (e: ApiException) {
                    showLog("error " + e.message)
                }
            }
        }


    override fun initUI(savedInstanceState: Bundle?) {
        setStatusBar()
        binding.initProfileInformation()
        binding.initBindings()
    }

    private fun FragmentProfileBinding.initBindings() {
        clickListeners()
        setLanguage()
        checkIfUserAnonymous()
        initGoogleSignBtn()
    }

    private fun FragmentProfileBinding.initProfileInformation(user: FirebaseUser? = viewModel.firebaseAuthInstance.currentUser) {
        if (user != null) {
            userEmail.text = user.email
            userName.text = user.displayName
            userName.isVisible = !user.displayName.isNullOrBlank()
            imageContainer.isVisible = user.photoUrl != null
            if (user.photoUrl != null)
                Glide.with(root.context)
                    .load(user.photoUrl)
                    .into(userImage)
        }
    }

    private fun FragmentProfileBinding.clickListeners() {
        languageOfTheApp.setOnClickListener {
            LanguageOfTheAppDialogFragment(this@ProfileFragment)
                .show(childFragmentManager, "LanguageOfTheAppDialogFragment")
        }
        logoutContainer.setOnClickListener {
            logout()
        }
        register.setOnClickListener {
            onClickRegistration()
        }
        googleSignIn.setOnClickListener {
            registerWithGmail()
        }
    }

    private fun FragmentProfileBinding.setLanguage() {
        if (viewModel.appLanguage.isBlank()) {
            systemLanguage.text = getString(R.string.system_language)
        } else {
            systemLanguage.text =
                AvailableLanguages.getLanguageNameByCode(requireContext(), viewModel.appLanguage)
        }
    }

    private fun setStatusBar() {
        requireActivity().window.statusBarColor = resources.getColor(R.color.background, null)
    }

    private fun FragmentProfileBinding.checkIfUserAnonymous() {
        loginContainer.isVisible = viewModel.isAnonymous()
        profileInformation.isVisible = !viewModel.isAnonymous()
    }

    private fun FragmentProfileBinding.initGoogleSignBtn() {
        googleSignIn.getChildAt(0)?.let {
            (it as TextView).text = getString(R.string.google_sign)
            val smaller = it.paddingLeft.coerceAtMost(it.paddingRight)
            it.setPadding(smaller, it.paddingTop, smaller, it.paddingBottom)
        }
    }

    private fun onClickRegistration() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        when {
            email.isBlank() -> {
                binding.emailContainer.error = getStr(R.string.email_empty)
            }
            password.isEmpty() -> {
                binding.passwordContainer.error = getStr(R.string.password_empty)
            }
            password.length < 5 -> {
                binding.passwordContainer.error = getStr(R.string.password_empty)
            }
            else -> {
                createUserFromAnonymous(email, password)
            }
        }
    }

    private fun createUserFromAnonymous(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        viewModel.firebaseAuthInstance.currentUser?.linkWithCredential(credential)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    sendVerificationEmail()
                } else {
                    toast("${it.exception?.message}")
                }
            }
    }

    private fun sendVerificationEmail() {
        viewModel.firebaseAuthInstance.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    toast(R.string.we_send_verification)
                } else {
                    toast(R.string.we_couldnt_send_verification)
                }
            }
    }

    override fun onSelectSystemLanguage(language: Language) {
        viewModel.setAppLanguage(language)
        LocaleHelper.setLocale(requireContext(), "en")
        restartActivity()
    }

    private fun registerWithGmail() {
        val intent = Intent(viewModel.googleSignInClient.signInIntent)
        launchSomeActivity.launch(intent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        viewModel.firebaseAuthInstance.currentUser?.linkWithCredential(credential)
            ?.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    toast(getString(R.string.linked_successfully))
                } else {
                    toast(task.exception?.message.toString())
                }
            }
    }

    private fun restartActivity() {
        startActivity(
            Intent(requireContext(), MainActivity::class.java).run {
                putExtras(Bundle().apply {
                    putBoolean(SHOULD_I_OPEN_PROFILE_FRAGMENT, true)
                })
            })
    }

    private fun logout() {
        if (viewModel.isAnonymous()) {
            logoutAnonymousUser()
        } else {
            simpleLogout()
        }
    }

    private fun logoutAnonymousUser() {
        viewModel.firebaseAuthInstance.currentUser?.delete()
        navigateToLoginActivity()
    }

    private fun simpleLogout() {
        Firebase.auth.signOut()
        viewModel.googleSignInClient.signOut()
            .addOnCompleteListener(requireActivity()) {
                navigateToLoginActivity()
            }
    }

    private fun navigateToLoginActivity() {
        Intent(requireContext(), LoginActivity::class.java).also {
            startActivity(it)
        }
        requireActivity().finish()
    }
}