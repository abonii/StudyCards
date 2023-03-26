package abm.co.navigation.navhost.root

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.navhost.auth.graph.authNavGraph
import abm.co.navigation.navhost.card.graph.NewCardOrCategoryDestinations
import abm.co.navigation.navhost.card.graph.cardGraph
import abm.co.navigation.navhost.card.graph.newCardOrSetGraph
import abm.co.navigation.navhost.game.graph.gameGraph
import abm.co.navigation.navhost.main.MainScreen
import abm.co.navigation.navhost.userattributes.userAttributesNavGraph
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

private const val MAIN_ANIMATION_DURATION = 400
private const val FADING_OUT_ANIMATION_DURATION = 350
private const val FADING_IN_ANIMATION_DURATION = 450

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RootNavHost(
    startDestination: String,
    showMessage: suspend (MessageContent) -> Unit,
    navigateToNewCardOrCategory: State<NewCardOrCategoryDestinations>,
    modifier: Modifier = Modifier,
    navController: NavHostController =  rememberAnimatedNavController()
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        route = Graph.ROOT,
        contentAlignment = Alignment.Center,
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
        authNavGraph(
            navController = navController,
            showMessage = showMessage
        )
        userAttributesNavGraph(
            navController = navController,
            showMessage = showMessage
        )
        composable(route = Graph.MAIN) {
            MainScreen(
                startDestination = Graph.HOME,
                showMessage = showMessage,
                navigateToNewCardOrCategory = navigateToNewCardOrCategory
            )
        }
    }
}

object Graph {
    const val ROOT = "root_graph"
    const val AUTH = "auth_graph"
    const val HOME = "home_graph"
    const val MAIN = "main_graph"
    const val GAME = "game_graph"
    const val PROFILE = "profile_graph"
    const val CARD = "card_graph"
    const val USER_ATTRIBUTES = "user_attributes_graph"
    const val NEW_CARD_OR_CATEGORY_GRAPH = "new_card_or_set_graph"
}
