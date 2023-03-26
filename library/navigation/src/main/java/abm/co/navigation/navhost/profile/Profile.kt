package abm.co.navigation.navhost.profile

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.profile.ProfilePage
import abm.co.navigation.navhost.profile.graph.ProfileDestinations
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.profile(
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit,
    route: String = ProfileDestinations.Profile.route
) {
    composable(
        route = route,
        enterTransition = {
            EnterTransition.None
        },
        popEnterTransition = {
            EnterTransition.None
        },
        exitTransition = {
            ExitTransition.None
        },
        popExitTransition = {
            ExitTransition.None
        }
    ) {
        ProfilePage()
    }
}
