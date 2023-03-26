package abm.co.navigation.navhost.game.main

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.game.main.MainGamePage
import abm.co.navigation.navhost.game.graph.GameDestinations
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.mainGame(
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit,
    route: String = GameDestinations.MainGame.route
) {
    composable(
        route = route,
        enterTransition = {
            EnterTransition.None
        },
        popEnterTransition = {
            EnterTransition.None
        },
        exitTransition = {
            ExitTransition.None
        },
        popExitTransition = {
            ExitTransition.None
        }
    ) {
        MainGamePage()
    }
}
