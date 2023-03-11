package abm.co.navigation.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.authorization.signup.SignUpPage
import abm.co.navigation.Destinations
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.signUp(
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit,
) {
    composable(
        route = Destinations.SignUp.route
    ) {
        SignUpPage(
            showMessage = showMessage,
            onNavigateHomePage = {
                navController.navigate(Destinations.Home.route) {
                    popUpTo(Destinations.ChooseUserAttributes.route) {
                        inclusive = true
                    }
                }
            },
            onNavigateLoginPage = {
                navController.navigate(Destinations.Login.route) {
                    popUpTo(Destinations.WelcomeLogin.route)
                }
            }
        )
    }
}
