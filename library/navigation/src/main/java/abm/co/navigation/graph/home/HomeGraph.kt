package abm.co.navigation.graph.home

import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.navhost.home.home
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeGraph(
    route: String,
    showMessage: suspend (MessageContent) -> Unit,
    startDestination: String = HomeDestinations.Home.route
) {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(
        route = route,
        navController = navController,
        startDestination = startDestination
    ) {
        home(
            navController = navController,
            showMessage = showMessage
        )
    }
}

sealed class HomeDestinations(val route: String) {
    object Home : HomeDestinations("home_page")
}
