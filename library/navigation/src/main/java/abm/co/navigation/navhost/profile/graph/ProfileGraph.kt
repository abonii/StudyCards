package abm.co.navigation.navhost.profile.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.navhost.profile.profile
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProfileGraph(
    route: String,
    showMessage: suspend (MessageContent) -> Unit,
    startDestination: String = ProfileDestinations.Profile.route
) {
//    val navController = rememberAnimatedNavController()
//    AnimatedNavHost(
//        route = route,
//        navController = navController,
//        startDestination = startDestination
//    ) {
//        profile(
//            navController = navController,
//            showMessage = showMessage
//        )
//    }
}

sealed class ProfileDestinations(val route: String) {
    object Profile : ProfileDestinations("profile_page")
}
