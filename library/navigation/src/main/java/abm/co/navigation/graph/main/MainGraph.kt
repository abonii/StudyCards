package abm.co.navigation.graph.main

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.graph.root.Graph
import abm.co.navigation.graph.card.CollectionOfSetGraph
import abm.co.navigation.graph.game.GameGraph
import abm.co.navigation.graph.home.HomeGraph
import abm.co.navigation.graph.newcardorset.NewCardOrSetGraph
import abm.co.navigation.graph.profile.ProfileGraph
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainGraph(
    startDestination: String,
    navController: NavHostController,
    showMessage: suspend (MessageContent) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedNavHost(
        route = Graph.MAIN,
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Graph.HOME) {
            HomeGraph(
                route = Graph.HOME,
                showMessage = showMessage
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

//enterTransition = {
//    slideInHorizontally(
//        initialOffsetX = { it / 3 },
//        animationSpec = tween(durationMillis = MAIN_ANIMATION_DURATION)
//    ) + fadeIn(animationSpec = tween(durationMillis = FADING_IN_ANIMATION_DURATION))
//},
//exitTransition = {
//    fadeOut(animationSpec = tween(durationMillis = FADING_OUT_ANIMATION_DURATION))
//},
//popEnterTransition = {
//    slideInHorizontally(
//        initialOffsetX = { -it / 3 },
//        animationSpec = tween(durationMillis = MAIN_ANIMATION_DURATION)
//    ) + fadeIn(animationSpec = tween(durationMillis = FADING_IN_ANIMATION_DURATION))
//},
//popExitTransition = {
//    fadeOut(animationSpec = tween(durationMillis = FADING_OUT_ANIMATION_DURATION))
//}
