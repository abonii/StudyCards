package abm.co.navigation.navhost.card

import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.graph.newcardorset.NewCardOrSetDestinations
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.newCardOrSet(
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit
) {
    composable(
        route = NewCardOrSetDestinations.NewCardOrSet.route,
        enterTransition = {
            fadeIn(animationSpec = tween(durationMillis = 500))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(durationMillis = 500))
        }
    ) {
        // todo no cards page
    }
}
