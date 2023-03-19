package abm.co.navigation.navhost.game.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.navhost.game.game
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GameGraph(
    route: String,
    showMessage: suspend (MessageContent) -> Unit,
    startDestination: String = GameDestinations.Game.route,
) {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(
        route = route,
        navController = navController,
        startDestination = startDestination
    ) {
        game(
            navController = navController,
            showMessage = showMessage
        )
    }
}

sealed class GameDestinations(val route: String) {
    object Game : GameDestinations("game_page")
}
