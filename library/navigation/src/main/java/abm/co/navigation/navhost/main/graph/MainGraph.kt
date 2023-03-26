package abm.co.navigation.navhost.main.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.navhost.card.main.mainCard
import abm.co.navigation.navhost.game.main.mainGame
import abm.co.navigation.navhost.home.home
import abm.co.navigation.navhost.profile.graph.ProfileGraph
import abm.co.navigation.navhost.profile.profile
import abm.co.navigation.navhost.root.Graph
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun MainGraph(
    startDestination: String,
    navController: NavHostController,
    hostNavController: NavHostController,
    showMessage: suspend (MessageContent) -> Unit,
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        route = Graph.MAIN,
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        home(
            route = Graph.HOME,
            navController = navController,
            showMessage = showMessage,
            openDrawer = openDrawer,
            hostNavController = hostNavController
        )
        mainCard(
            route = Graph.COLLECTION_OF_SET,
            navController = navController,
            showMessage = showMessage
        )
        composable(Graph.NEW_CARD_OR_CATEGORY_GRAPH) {
            /* Empty */
        }
        mainGame(
            route = Graph.GAME,
            navController = navController,
            showMessage = showMessage
        )
        profile(
            route = Graph.PROFILE,
            navController = navController,
            showMessage = showMessage
        )
    }
}
