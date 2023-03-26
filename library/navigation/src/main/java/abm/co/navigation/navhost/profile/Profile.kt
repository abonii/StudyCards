package abm.co.navigation.navhost.profile

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.profile.ProfilePage
import abm.co.navigation.navhost.profile.graph.ProfileDestinations
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.profile(
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit,
    route: String = ProfileDestinations.Profile.route
) {
    composable(
        route = route
    ) {
        ProfilePage()
    }
}
