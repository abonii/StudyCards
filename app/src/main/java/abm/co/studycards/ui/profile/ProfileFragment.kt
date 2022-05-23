package abm.co.studycards.ui.profile

import abm.co.studycards.BuildConfig
import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.domain.model.AvailableLanguages
import abm.co.studycards.domain.model.Language
import abm.co.studycards.databinding.FragmentProfileBinding
import abm.co.studycards.helpers.LocaleHelper
import abm.co.studycards.ui.login.LoginActivity
import abm.co.studycards.util.Constants.DEV_ACCOUNT_LINK
import abm.co.studycards.util.Constants.REQUEST_SYSTEM_LANGUAGE_KEY
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.navigate
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseBindingFragment<FragmentProfileBinding>(R.layout.fragment_profile) {

    private val viewModel: ProfileViewModel by viewModels()

    private var launchSomeActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    viewModel.firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    toast(e.message.toString())
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.toast.collectLatest {
                toast(it)
            }
        }
    }

    override fun initUI(savedInstanceState: Bundle?) {
        binding.run {
            profileFragment = this@ProfileFragment
            viewmodel = viewModel
            version.text = getString(R.string.version, BuildConfig.VERSION_NAME)
            setLanguage()
            initGoogleSignBtn()
        }
        setStatusBar()
        setListeners()
    }

    private fun setListeners() {
        setFragmentResultListener(REQUEST_SYSTEM_LANGUAGE_KEY) { _, bundle ->
            val language = bundle.getParcelable<Language>("language")
            if (language != null && viewModel.appLanguage != language.code) {
                onSelectSystemLanguage(language)
            }
        }
    }

    fun navigateToChangeAppLanguage() {
        ProfileFragmentDirections.actionProfileFragmentToLanguageOfTheAppDialogFragment().also {
            navigate(it)
        }
    }

    fun navigateToPremiumFragment() {
        ProfileFragmentDirections.actionProfileFragmentToBuyPremiumFragment().also {
            navigate(it)
        }
    }

    private fun FragmentProfileBinding.setLanguage() {
        if (viewModel.appLanguage.isEmpty()) {
            systemLanguage.text = getString(R.string.system_language)
        } else {
            systemLanguage.text =
                AvailableLanguages.getLanguageNameByCode(requireContext(), viewModel.appLanguage)
        }
    }

    private fun setStatusBar() {
        requireActivity().window.statusBarColor = resources.getColor(R.color.background, null)
    }

    private fun FragmentProfileBinding.initGoogleSignBtn() {
        googleSignIn.getChildAt(0)?.let {
            (it as TextView).text = getString(R.string.google_sign)
            val smaller = it.paddingLeft.coerceAtMost(it.paddingRight)
            it.setPadding(smaller, it.paddingTop, smaller, it.paddingBottom)
        }
    }


    private fun onSelectSystemLanguage(language: Language) {
        viewModel.setAppLanguage(language)
        LocaleHelper.setLocale(requireContext(), "en")
        restartActivity()
    }

    fun registerWithGmail() {
        val intent = Intent(viewModel.googleSignInClient.signInIntent)
        launchSomeActivity.launch(intent)
    }

    private fun restartActivity() {
        startActivity(
            Intent(requireContext(), MainActivity::class.java)
        )
        requireActivity().finish()
    }

    fun logout() {
        if (viewModel.isAnonymous.value) {
            logoutAnonymousUser()
        } else {
            simpleLogout()
        }
    }

    fun onClickContactUs() {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(DEV_ACCOUNT_LINK)
        startActivity(i)
    }

    fun onChangePassword() {
        val n = ProfileFragmentDirections.actionProfileFragmentToChangePasswordFragment()
        navigate(n)
    }

    private fun logoutAnonymousUser() {
        viewModel.removeDatabaseOfUser {
            navigateToLoginActivity()
        }
    }

    private fun simpleLogout() {
        viewModel.simpleLogout {
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