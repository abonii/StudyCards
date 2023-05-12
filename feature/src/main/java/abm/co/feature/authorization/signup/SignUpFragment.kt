package abm.co.feature.authorization.signup

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
class SignUpFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = SignUpFragment.rootViewId

    @Inject
    lateinit var navigationBetweenModules: NavigationBetweenModules

    @Composable
    override fun InitUI(messageContent: messageContent) {
        SignUpPage(
            showMessage = {
                messageContent(it)
            },
            onNavigateChooseUserAttributes = {
                navigationBetweenModules.navigateFromAuthorizationToUserPreferenceAndLanguage(
                    showAdditionQuiz = true,
                    navController = findNavController()
                )
            },
            onNavigateLoginPage = {
                findNavController().navigateSafe(
                    SignUpFragmentDirections.toLoginDestination()
                )
            }
        )
    }
}