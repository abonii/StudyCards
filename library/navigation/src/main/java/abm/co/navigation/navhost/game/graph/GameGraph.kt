package abm.co.navigation.navhost.game.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.navhost.game.main.mainGame
import abm.co.navigation.navhost.game.swipe.swipeGame
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GameGraph(
    route: String,
    showMessage: suspend (MessageContent) -> Unit,
    startDestination: String = GameDestinations.MainGame.route,
) {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(
        route = route,
        navController = navController,
        startDestination = startDestination
    ) {
        mainGame(
            navController = navController,
            showMessage = showMessage
        )
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
