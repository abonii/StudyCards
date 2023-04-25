package abm.co.feature.authorization.welcomelogin

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
class WelcomeLoginFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = Companion.rootViewId

    @Inject
    lateinit var navigationBetweenModules: NavigationBetweenModules

    @Composable
    override fun InitUI(messageContent: messageContent) {
        WelcomeLoginPage(
            showMessage = messageContent,
            onNavigateChooseUserAttributes = {
                findNavController().navigateSafe(
                    navigationBetweenModules.getNavigateFromAuthorizationToUserPreferenceAndLanguage()
                )
            },
            onNavigateSignUpPage = {
                findNavController().navigate(
                    WelcomeLoginFragmentDirections.toSignUpDestination()
                )
            },
            onNavigateToLoginPage = {
                findNavController().navigateSafe(
                    WelcomeLoginFragmentDirections.toLoginDestination()
                )
            }
        )
    }
}