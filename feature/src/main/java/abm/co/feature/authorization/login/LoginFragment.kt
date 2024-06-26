package abm.co.feature.authorization.login

import abm.co.core.navigation.NavigationBetweenModules
import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import abm.co.designsystem.navigation.extension.navigateSafe
import android.view.View
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    @Inject
    lateinit var navigationBetweenModules: NavigationBetweenModules

    override val rootViewId: Int get() = LoginFragment.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        LoginPage(
            onNavigateToChooseUserAttributes = {
                navigationBetweenModules.navigateFromAuthorizationToUserPreferenceAndLanguage(
                    showAdditionQuiz = false,
                    navController = findNavController()
                )
            },
            onNavigateToMainPage = {
                navigationBetweenModules.navigateFromAuthorizationToMain(findNavController())
            },
            onNavigateToSignUpPage = {
                findNavController().navigateSafe(
                    LoginFragmentDirections.toSignUpDestination()
                )
            },
            onNavigateToForgotPage = {
                findNavController().navigateSafe(
                    LoginFragmentDirections.toForgotPasswordDestination()
                )
            },
            showMessage = messageContent
        )
    }
}