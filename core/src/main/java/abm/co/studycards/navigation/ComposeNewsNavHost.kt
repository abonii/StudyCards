package abm.co.studycards.navigation

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.graph.chooseUserAttributes
import abm.co.navigation.graph.home
import abm.co.navigation.graph.login
import abm.co.navigation.graph.signUp
import abm.co.navigation.graph.welcomeLogin
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost

private const val MAIN_ANIMATION_DURATION = 400
private const val FADING_OUT_ANIMATION_DURATION = 350
private const val FADING_IN_ANIMATION_DURATION = 450

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ComposeNewsNavHost(
    startDestination: String,
    navController: NavHostController,
    showMessage: suspend (MessageContent) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
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
        welcomeLogin(
            navController = navController,
            showMessage = showMessage
        )
        login(
            navController = navController,
            showMessage = showMessage
        )
        signUp(
            navController = navController,
            showMessage = showMessage
        )
        home(
            navController = navController,
            showMessage = showMessage
        )
        chooseUserAttributes(
            navController = navController,
            showMessage = showMessage
        )
    }
}
