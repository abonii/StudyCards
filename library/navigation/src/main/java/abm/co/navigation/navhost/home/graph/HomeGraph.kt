package abm.co.navigation.navhost.home.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.navhost.card.category.category
import abm.co.navigation.navhost.home.home
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

private const val MAIN_ANIMATION_DURATION = 400
private const val FADING_OUT_ANIMATION_DURATION = 350
private const val FADING_IN_ANIMATION_DURATION = 450

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeGraph(
    route: String,
    openDrawer: () -> Unit,
    hostNavController: NavController,
    showMessage: suspend (MessageContent) -> Unit,
    startDestination: String = HomeDestinations.Home.route,
    bottomNavVisible: MutableState<Boolean>
) {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(
        route = route,
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it / 3 },
                animationSpec = tween(durationMillis = MAIN_ANIMATION_DURATION)
            ) + fadeIn(animationSpec = tween(durationMillis = FADING_IN_ANIMATION_DURATION))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(durationMillis = FADING_OUT_ANIMATION_DURATION))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec = tween(durationMillis = MAIN_ANIMATION_DURATION)
            ) + fadeIn(animationSpec = tween(durationMillis = FADING_IN_ANIMATION_DURATION))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(durationMillis = FADING_OUT_ANIMATION_DURATION))
        }
    ) {
        home(
            navController = navController,
            showMessage = showMessage,
            openDrawer = openDrawer
        )
        category(
            navController = navController,
            showMessage = showMessage,
        )
    }
}

sealed class HomeDestinations(val route: String) {
    object Home : HomeDestinations("home_page")
}
