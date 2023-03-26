package abm.co.navigation.navhost.card.category

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.card.editcategory.EditCategoryPage
import abm.co.navigation.navhost.card.graph.NewCardOrCategoryDestinations
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.editCategory(
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit
) {
    composable(
        route = NewCardOrCategoryDestinations.Category.route,
        deepLinks = listOf(navDeepLink {
            uriPattern = NewCardOrCategoryDestinations.Category.deepLink
        })
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
