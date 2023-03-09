package abm.co.navigation.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.home.HomePage
import abm.co.navigation.Destinations
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.home(
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit
) {
    composable(
        route = Destinations.Home.route,
        enterTransition = {
            fadeIn(animationSpec = tween(durationMillis = 500))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(durationMillis = 500))
        }
    ) {
        HomePage()
    }
}
