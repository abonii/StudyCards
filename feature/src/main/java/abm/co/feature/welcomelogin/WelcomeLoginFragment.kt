package abm.co.feature.welcomelogin

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import android.view.View
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeLoginFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = WelcomeLoginFragment.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        WelcomeLoginPage(
            showMessage = messageContent,
            onNavigateChooseUserAttributes = {
//                navController.navigate(
//                    route = Graph.USER_ATTRIBUTES,
//                    args = bundleOf(
//                        ChooseUserAttributesDestination().showAdditionQuiz to true
//                    ),
//                    navOptions = NavOptions.Builder().apply {
//                        setPopUpTo(Graph.AUTH, inclusive = true)
//                    }.build()
//                ) todo navigation
            },
            onNavigateSignUpPage = {
//                navController.navigate(AuthDestinations.SignUp.route) todo navigation
            },
            onNavigateToLoginPage = {
//                navController.navigate(AuthDestinations.Login.route) todo navigation
            }
        )
    }
}