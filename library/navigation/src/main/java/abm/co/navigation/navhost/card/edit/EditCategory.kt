package abm.co.navigation.navhost.card.edit

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.card.category.EditCategoryPage
import abm.co.navigation.navhost.card.graph.NewCardOrCategoryDestinations
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

fun NavGraphBuilder.editCategory(
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit
) {
    composable(
        route = NewCardOrCategoryDestinations.Category.route,
        deepLinks = listOf(navDeepLink { uriPattern = NewCardOrCategoryDestinations.Category.deepLink })
    ) {
        EditCategoryPage(
            onBack = navController::navigateUp,
            navigateToNewCard = {
                navController.navigate(NewCardOrCategoryDestinations.Card.route)
            },
            showMessage = showMessage
        )
    }
}
