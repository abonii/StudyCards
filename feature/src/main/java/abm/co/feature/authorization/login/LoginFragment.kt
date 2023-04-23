package abm.co.feature.authorization.login

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import android.view.View
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment: BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = LoginFragment.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        LoginPage(
            onNavigateChooseUserAttributes = {
//                navController.navigate(Graph.MAIN) {
//                    popUpTo(Graph.AUTH) { inclusive = true } todo navigation
//                }
            },
            onNavigateHomePage = {
//                navController.navigate(Graph.MAIN) {
//                    popUpTo(Graph.AUTH) { inclusive = true } todo navigation
//                }
            },
            onNavigateSignUpPage = {
//                navOptionsController.navigate(
//                    route = AuthDestinations.SignUp.route
//                ) {
//                    popUpTo(AuthDestinations.WelcomeLogin.route) todo navigation
//                }
            },
            onNavigateToForgotPage = {

            },
            showMessage = {
                messageContent(it)
            }
        )
    }
}