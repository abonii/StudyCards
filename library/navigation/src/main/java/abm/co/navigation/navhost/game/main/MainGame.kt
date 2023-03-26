package abm.co.navigation.navhost.game.main

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.game.main.MainGamePage
import abm.co.navigation.navhost.game.graph.GameDestinations
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.mainGame(
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit,
    route: String = GameDestinations.MainGame.route
) {
    composable(
        route = route
    ) {
        MainGamePage()
    }
}
