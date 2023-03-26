package abm.co.navigation.navhost.card.main

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.card.main.MainCardPage
import abm.co.navigation.navhost.card.graph.CardDestinations
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.mainCard(
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit,
    route: String = CardDestinations.MainCard.route
) {
    composable(
        route = route
    ) {
        MainCardPage()
    }
}
