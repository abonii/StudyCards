package abm.co.navigation.navhost.home

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.home.HomePage
import abm.co.navigation.navhost.game.graph.GameDestinations
import abm.co.navigation.navhost.home.graph.HomeDestinations
import abm.co.navigation.navhost.root.Graph
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.home(
    navController: NavController,
    hostNavController: NavController,
    openDrawer: () -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    route: String = HomeDestinations.Home.route
) {
    composable(
        route = route
    ) {
        HomePage(
            showMessage = showMessage,
            onNavigateToLanguageSelectPage = {
                navController.navigate(Graph.PROFILE)
            },
            openDrawer = openDrawer,
            navigateToAllCategory = {},
            navigateToCategory = {},
            navigateToCategoryGame = {
                hostNavController.navigate(GameDestinations.SwipeGame.route)
            }
        )
    }
}
