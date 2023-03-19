package abm.co.navigation.navhost.card.category

import abm.co.designsystem.message.common.MessageContent
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
        route = "Destinations.Category.route",
        enterTransition = {
            fadeIn(animationSpec = tween(durationMillis = 500))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(durationMillis = 500))
        }
    ) {
        // TODO no word page
    }
}
