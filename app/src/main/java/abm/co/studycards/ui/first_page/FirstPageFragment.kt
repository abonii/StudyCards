package abm.co.studycards.ui.first_page

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentFirstPageBinding
import abm.co.studycards.ui.login.LoginEventChannel
import abm.co.studycards.ui.login.LoginViewModel
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import abm.co.studycards.util.navigate
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FirstPageFragment :
    BaseBindingFragment<FragmentFirstPageBinding>(R.layout.fragment_first_page) {

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
            }else
                viewModel.setLoading(false)
        }

    private val viewModel: LoginViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) = binding.run {
        viewmodel = viewModel
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.sharedFlow.collect { eventChannel ->
                when (eventChannel) {
                    LoginEventChannel.NavigateToMainActivity -> {
                        navigateToMainActivity()
                    }
                    LoginEventChannel.NavigateToEmailFragment -> {
                        navigateToEmailFragment()
                    }
                    is LoginEventChannel.LoginViaGoogle -> {
                        launchSomeActivity.launch(eventChannel.intent)
                    }
                    LoginEventChannel.NavigateToRegistration -> {
                        navigateToRegistration()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun navigateToEmailFragment() {
        FirstPageFragmentDirections.actionFirstPageFragmentToLoginFragment().also {
            navigate(it)
        }
    }

    private fun navigateToMainActivity() {
        Intent(requireContext(), MainActivity::class.java).also {
            startActivity(it)
        }
        requireActivity().finish()
    }

    private fun navigateToRegistration() {
        FirstPageFragmentDirections.actionFirstPageFragmentToRegistrationFragment().also {
            navigate(it)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkUserExistence()
    }

}