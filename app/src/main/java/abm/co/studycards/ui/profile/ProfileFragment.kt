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
import abm.co.studycards.util.base.BaseBindingFragment
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ProfileFragment : BaseBindingFragment<FragmentProfileBinding>(R.layout.fragment_profile),
    LanguageOfTheAppAdapter.OnClick {
    companion object {
        const val SHOULD_I_OPEN_PROFILE_FRAGMENT = "OPEN_PROFILE_FRAGMENT"
    }

    private val viewModel: ProfileViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        setStatusBar()
        setLanguage()
        initProfileInformation()
        initBindings()
        clickListeners()
    }

    private fun initProfileInformation() {
        val user = viewModel.firebaseAuthInstance.currentUser
        if (user != null) {
            binding.apply {
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
    }

    private fun clickListeners() {
        binding.apply {
            languageOfTheApp.setOnClickListener {
                LanguageOfTheAppDialogFragment(this@ProfileFragment)
                    .show(childFragmentManager, "LanguageOfTheAppDialogFragment")
            }
//            targetLanguageContainer.setOnClickListener {
//                toSelectTargetLanguage()
//            }
            logoutContainer.setOnClickListener {
                logout()
            }
        }
    }

    private fun setLanguage() {
        if (viewModel.appLanguage.isBlank()) {
            binding.systemLanguage.text = getString(R.string.system_language)
        } else {
            binding.systemLanguage.text =
                AvailableLanguages.getLanguageNameByCode(requireContext(), viewModel.appLanguage)
        }
    }

    private fun initBindings() {

    }

    private fun setStatusBar() {
        requireActivity().window.statusBarColor = resources.getColor(R.color.background, null)
    }

    override fun onClick(language: Language) {
        viewModel.setAppLanguage(language)
        LocaleHelper.setLocale(requireContext(), "en")
        restartActivity()
    }

    private fun restartActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        val bundle = Bundle()
        bundle.putBoolean(SHOULD_I_OPEN_PROFILE_FRAGMENT, true)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun logout() {
        Firebase.auth.signOut()
        viewModel.googleSignInClient.signOut()
            .addOnCompleteListener(requireActivity()) {
                val i = Intent(requireContext(), LoginActivity::class.java)
                startActivity(i)
                requireActivity().finish()
            }
    }
}