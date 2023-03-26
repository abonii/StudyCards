package abm.co.navigation.navhost.game.swipe

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.game.swipe.SwipeGamePage
import abm.co.navigation.navhost.game.graph.GameDestinations
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.swipeGame(
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit
) {
    composable(
        route = GameDestinations.SwipeGame.route
    ) {
        SwipeGamePage(
            onBack = navController::navigateUp,
            showMessage = showMessage
        )
    }
}
