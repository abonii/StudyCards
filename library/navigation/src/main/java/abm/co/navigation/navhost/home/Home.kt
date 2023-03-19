package abm.co.navigation.navhost.home

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.home.HomePage
import abm.co.navigation.navhost.home.graph.HomeDestinations
import abm.co.navigation.navhost.root.Graph
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.home(
    navController: NavController,
    mainController: NavController,
    openDrawer: () -> Unit,
    showMessage: suspend (MessageContent) -> Unit
) {
    composable(
        route = HomeDestinations.Home.route
    ) {
        HomePage(
            showMessage = showMessage,
            onNavigateToLanguageSelectPage = {
                mainController.navigate(Graph.PROFILE)
            },
            openDrawer = openDrawer,
            navigateToAllCategory = {},
            navigateToCategory = {},
            navigateToCategoryGame = {}
        )
    }
}
