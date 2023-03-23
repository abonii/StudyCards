package abm.co.navigation.navhost.card.edit

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.card.card.EditCardPage
import abm.co.navigation.navhost.card.graph.NewCardOrCategoryDestinations
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

fun NavGraphBuilder.editCard(
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit
) {
    composable(
        route = NewCardOrCategoryDestinations.Card.route,
        deepLinks = listOf(navDeepLink { uriPattern = NewCardOrCategoryDestinations.Card.deepLink })
    ) {
        EditCardPage()
    }
}
