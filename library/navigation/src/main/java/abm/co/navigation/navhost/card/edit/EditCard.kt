package abm.co.navigation.navhost.card.edit

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.card.card.EditCardPage
import abm.co.navigation.navhost.card.graph.NewCardOrSetDestinations
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

fun NavGraphBuilder.editCard(
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit
) {
    composable(
        route = NewCardOrSetDestinations.Card.route,
        deepLinks = NewCardOrSetDestinations.Card.deepLink?.let {
            listOf(navDeepLink { uriPattern = it })
        } ?: emptyList()
    ) {
        EditCardPage()
    }
}
