package abm.co.navigation.navhost.card.card

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.card.card.EditCardPage
import abm.co.navigation.navhost.card.graph.NewCardOrCategoryDestinations
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
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
