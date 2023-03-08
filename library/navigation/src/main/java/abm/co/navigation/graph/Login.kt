package abm.co.navigation.graph

import abm.co.domain.base.Failure
import abm.co.feature.login.LoginPage
import abm.co.navigation.Destinations
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.login(
    onFailure: (Failure) -> Unit,
    navController: NavController
) {
    composable(Destinations.Login.route) {
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
            onNavigateToEmailPage = {

            },
            onNavigateToForgotPage = {

            },
            onFailure = onFailure
        )
    }
}
