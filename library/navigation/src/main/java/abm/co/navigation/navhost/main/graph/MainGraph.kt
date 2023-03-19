package abm.co.navigation.navhost.main.graph

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.navhost.card.graph.MainCardGraph
import abm.co.navigation.navhost.card.graph.newCardOrSetGraph
import abm.co.navigation.navhost.game.graph.GameGraph
import abm.co.navigation.navhost.home.graph.HomeGraph
import abm.co.navigation.navhost.profile.graph.ProfileGraph
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
        composable(Graph.HOME) {
            HomeGraph(
                route = Graph.HOME,
                showMessage = showMessage,
                openDrawer = openDrawer,
                mainController = navController
            )
        }
        composable(Graph.COLLECTION_OF_SET) {
            MainCardGraph(
                route = Graph.COLLECTION_OF_SET,
                showMessage = showMessage
            )
        }
        newCardOrSetGraph(
            route = Graph.NEW_CARD_OR_SET_GRAPH,
            navController = navController,
            showMessage = showMessage
        )
        composable(Graph.GAME) {
            GameGraph(
                route = Graph.GAME,
                showMessage = showMessage
            )
        }
        composable(Graph.PROFILE) {
            ProfileGraph(
                route = Graph.PROFILE,
                showMessage = showMessage
            )
        }
    }
}
