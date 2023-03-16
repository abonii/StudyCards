package abm.co.navigation.graph.main

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.graph.card.CollectionOfSetGraph
import abm.co.navigation.graph.game.GameGraph
import abm.co.navigation.graph.home.HomeGraph
import abm.co.navigation.graph.newcardorset.NewCardOrSetGraph
import abm.co.navigation.graph.profile.ProfileGraph
import abm.co.navigation.graph.root.Graph
import androidx.compose.animation.ExperimentalAnimationApi
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
            CollectionOfSetGraph(
                route = Graph.COLLECTION_OF_SET,
                showMessage = showMessage
            )
        }
        composable(Graph.NEW_CARD_OR_SET_GRAPH) {
            NewCardOrSetGraph(
                route = Graph.NEW_CARD_OR_SET_GRAPH,
                showMessage = showMessage
            )
        }
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
