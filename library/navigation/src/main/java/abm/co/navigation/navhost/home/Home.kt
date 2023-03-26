package abm.co.navigation.navhost.home

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.home.HomePage
import abm.co.navigation.extension.navigate
import abm.co.navigation.navhost.card.graph.CardDestinations
import abm.co.navigation.navhost.game.graph.GameDestinations
import abm.co.navigation.navhost.home.graph.HomeDestinations
import abm.co.navigation.navhost.root.Graph
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.home(
    navController: NavController,
    openDrawer: () -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    route: String = HomeDestinations.Home.route,
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
        HomePage(
            showMessage = showMessage,
            onNavigateToLanguageSelectPage = {
                navController.navigate(Graph.PROFILE)
            },
            openDrawer = openDrawer,
            navigateToAllCategory = {},
            navigateToCategory = { category ->
                navController.navigate(
                    route = CardDestinations.Category().route,
                    args = bundleOf(
                        CardDestinations.Category().category to category
                    ),
                    navOptions = NavOptions.Builder().apply {
                        this.setEnterAnim(android.R.anim.slide_in_left)
                    }.build()
                )
            },
            navigateToCategoryGame = {
                navController.navigate(GameDestinations.SwipeGame.route)
            }
        )
    }
}
