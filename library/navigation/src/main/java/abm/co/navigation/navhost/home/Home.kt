package abm.co.navigation.navhost.home

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.home.HomePage
import abm.co.navigation.graph.home.HomeDestinations
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.home(
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit
) {
    composable(
        route = HomeDestinations.Home.route
    ) {
        HomePage()
    }
}
