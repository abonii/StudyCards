package abm.co.navigation.graph

import abm.co.feature.registration.RegistrationPage
import abm.co.navigation.Destinations
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.registration(
    navController: NavController
) {
    composable(Destinations.Registration.route) {
        RegistrationPage()
    }
}
