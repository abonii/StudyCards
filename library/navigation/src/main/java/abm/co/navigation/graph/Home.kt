package abm.co.navigation.graph

import abm.co.feature.home.HomePage
import abm.co.navigation.Destinations
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.home(
    navController: NavController,
) {
    composable(Destinations.Home.route) {
        HomePage()
    }
}

fun NavGraphBuilder.home2(
    navController: NavController,
) {
    composable(Destinations.Home2.route) {
        HomePage()
    }
}
fun NavGraphBuilder.home3(
    navController: NavController,
) {
    composable(Destinations.Home3.route) {
        HomePage()
    }
}