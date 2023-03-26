package abm.co.navigation.navhost.home.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.navhost.home.home
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeGraph(
    route: String,
    openDrawer: () -> Unit,
    mainController: NavController,
    hostNavController: NavController,
    showMessage: suspend (MessageContent) -> Unit,
    startDestination: String = HomeDestinations.Home.route
) {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(
        route = route,
        navController = navController,
        startDestination = startDestination
    ) {
//        home(
//            navController = navController,
//            mainController = mainController,
//            showMessage = showMessage,
//            openDrawer = openDrawer,
//            hostNavController = hostNavController
//        )
    }
}

sealed class HomeDestinations(val route: String) {
    object Home : HomeDestinations("home_page")
}
