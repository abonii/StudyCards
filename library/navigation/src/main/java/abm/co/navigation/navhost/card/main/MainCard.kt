package abm.co.navigation.navhost.card.main

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.card.main.MainCardPage
import abm.co.navigation.navhost.card.graph.CardDestinations
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.mainCard(
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit,
    route: String = CardDestinations.MainCard.route
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
        MainCardPage()
    }
}
