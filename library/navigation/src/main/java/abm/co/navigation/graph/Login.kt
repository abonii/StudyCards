package abm.co.navigation.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.login.LoginPage
import abm.co.navigation.Destinations
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
        route = Destinations.Login.route
    ) {
        LoginPage(
            onNavigateHomePage = {
                navController.navigate(
                    route = Destinations.Home.route
                )
            },
            onNavigateRegistrationPage = {
                navController.navigate(
                    route = Destinations.Registration.route
                )
            },
            onNavigateToForgotPage = {

            },
            showMessage = showMessage
        )
    }
}
