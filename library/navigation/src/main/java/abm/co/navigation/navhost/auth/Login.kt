package abm.co.navigation.navhost.auth

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.authorization.login.LoginPage
import abm.co.navigation.navhost.auth.graph.AuthDestinations
import abm.co.navigation.navhost.root.Graph
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.login(
    showMessage: suspend (MessageContent) -> Unit,
    navController: NavController
) {
    composable(
        route = AuthDestinations.Login.route
    ) {
        LoginPage(
            onNavigateChooseUserAttributes = {
                navController.navigate(Graph.MAIN) {
                    popUpTo(Graph.AUTH) { inclusive = true }
                }
            },
            onNavigateHomePage = {
                navController.navigate(Graph.MAIN) {
                    popUpTo(Graph.AUTH) { inclusive = true }
                }
            },
            onNavigateSignUpPage = {
                navController.navigate(
                    route = AuthDestinations.SignUp.route
                ) {
                    popUpTo(AuthDestinations.WelcomeLogin.route)
                }
            },
            onNavigateToForgotPage = {

            },
            showMessage = showMessage
        )
    }
}
