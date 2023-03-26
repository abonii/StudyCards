package abm.co.navigation.navhost.card.category

import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.navhost.card.graph.CardDestinations
import abm.co.navigation.navhost.card.graph.NewCardOrCategoryDestinations
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.category(
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit
) {
    composable(
        route = CardDestinations.Category.route
    ) {
        // TODO no word page
    }
}
