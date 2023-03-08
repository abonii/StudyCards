package abm.co.navigation.graph

import abm.co.domain.base.Failure
import abm.co.feature.welcomelogin.WelcomeLoginPage
import abm.co.navigation.Destinations
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.welcomeLogin(
    onFailure: (Failure) -> Unit,
    navController: NavController
) {
    composable(Destinations.WelcomeLogin.route) {
        WelcomeLoginPage(
            onFailure = onFailure,
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
