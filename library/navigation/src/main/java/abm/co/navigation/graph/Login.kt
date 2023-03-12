package abm.co.navigation.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.authorization.login.LoginPage
import abm.co.navigation.Destinations
import abm.co.navigation.extensionfunction.navigate
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.login(
    showMessage: suspend (MessageContent) -> Unit,
    navController: NavController
) {
    composable(
        route = Destinations.Login.route
    ) {
        LoginPage(
            onNavigateChooseUserAttributes = {
                navController.navigate(Destinations.ChooseUserAttributes().route) {
                    popUpTo(Destinations.Login.route) {
                        inclusive = true
                    }
                }
            },
            onNavigateHomePage = {
                navController.navigate(Destinations.Home.route) {
                    popUpTo(Destinations.Login.route) {
                        inclusive = true
                    }
                }
            },
            onNavigateSignUpPage = {
                navController.navigate(
                    route = Destinations.SignUp.route
                ) {
                    popUpTo(Destinations.WelcomeLogin.route)
                }
            },
            onNavigateToForgotPage = {

            },
            showMessage = showMessage
        )
    }
}
