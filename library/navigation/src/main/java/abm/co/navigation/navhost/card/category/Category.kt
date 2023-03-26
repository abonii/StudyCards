package abm.co.navigation.navhost.card.category

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.card.category.CategoryPage
import abm.co.navigation.extension.navigate
import abm.co.navigation.navhost.card.graph.CardDestinations
import abm.co.navigation.navhost.card.graph.NewCardOrCategoryDestinations
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.category(
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit,
) {
    composable(
        route = CardDestinations.Category().route,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it / 3 },
                animationSpec = tween(durationMillis = 400)
            ) + fadeIn(animationSpec = tween(durationMillis = 400))
        },
        popEnterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        CategoryPage(
            showMessage = showMessage,
            onBack = navController::navigateUp,
            navigateToCard = {
                navController.navigate(
                    route = NewCardOrCategoryDestinations.Card().route,
                    args = bundleOf(NewCardOrCategoryDestinations.Card().card to it)
                )
            },
            navigateToEditCategory = {

            }
        )
    }
}
