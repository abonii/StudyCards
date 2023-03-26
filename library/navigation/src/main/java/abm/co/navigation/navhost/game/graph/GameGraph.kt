package abm.co.navigation.navhost.game.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.navhost.game.swipe.swipeGame
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation

fun NavGraphBuilder.gameGraph(
    route: String,
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit,
    startDestination: String = GameDestinations.MainGame.route,
) {
    navigation(
        route = route,
        startDestination = startDestination
    ) {
        swipeGame(
            navController = navController,
            showMessage = showMessage
        )
    }
}

sealed class GameDestinations(val route: String) {
    object MainGame : GameDestinations("game_page")
    object SwipeGame : GameDestinations("swipe_game_page")
}
