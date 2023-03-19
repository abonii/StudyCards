package abm.co.navigation.navhost.card.edit

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.card.setofcards.EditSetOfCardsPage
import abm.co.navigation.navhost.card.graph.NewCardOrSetDestinations
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

fun NavGraphBuilder.editSetOfCards(
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit
) {
    composable(
        route = NewCardOrSetDestinations.SetOfCards.route,
        deepLinks = NewCardOrSetDestinations.SetOfCards.deepLink?.let {
            listOf(navDeepLink { uriPattern = it })
        } ?: emptyList()
    ) {
        EditSetOfCardsPage()
    }
}
