package abm.co.navigation.navhost.profile

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.profile.ProfilePage
import abm.co.navigation.graph.profile.ProfileDestinations
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.profile(
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit
) {
    composable(
        route = ProfileDestinations.Profile.route,
        enterTransition = {
            fadeIn(animationSpec = tween(durationMillis = 500))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(durationMillis = 500))
        }
    ) {
        ProfilePage()
    }
}
