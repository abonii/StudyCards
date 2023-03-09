package abm.co.navigation.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.welcomelogin.WelcomeLoginPage
import abm.co.navigation.Destinations
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.welcomeLogin(
    showMessage: suspend (MessageContent) -> Unit,
    navController: NavController
) {
    composable(
        route = Destinations.WelcomeLogin.route
    ) {
        WelcomeLoginPage(
            showMessage = showMessage,
            onNavigateRegistrationPage = {
                navController.navigate(Destinations.Registration.route)
            },
            onNavigateHomePage = {
                navController.navigate(Destinations.Home.route)
            },
            onNavigateToLoginPage = {
                navController.navigate(Destinations.Login.route)
            }
        )
    }
}
