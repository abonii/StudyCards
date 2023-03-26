package abm.co.navigation.navhost.main.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.navhost.card.card.editCard
import abm.co.navigation.navhost.card.category.category
import abm.co.navigation.navhost.card.category.editCategory
import abm.co.navigation.navhost.card.main.mainCard
import abm.co.navigation.navhost.game.main.mainGame
import abm.co.navigation.navhost.game.swipe.swipeGame
import abm.co.navigation.navhost.home.home
import abm.co.navigation.navhost.profile.profile
import abm.co.navigation.navhost.root.Graph
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainGraph(
    startDestination: String,
    navController: NavHostController,
    openDrawer: () -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedNavHost(
        route = Graph.MAIN,
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        home(
            route = Graph.HOME,
            navController = navController,
            showMessage = showMessage,
            openDrawer = openDrawer
        )
        mainCard(
            route = Graph.CARD,
            navController = navController,
            showMessage = showMessage
        )
        composable(Graph.NEW_CARD_OR_CATEGORY_GRAPH) {
            /* Empty */
        }
        mainGame(
            route = Graph.GAME,
            navController = navController,
            showMessage = showMessage
        )
        profile(
            route = Graph.PROFILE,
            navController = navController,
            showMessage = showMessage
        )
        category(
            navController = navController,
            showMessage = showMessage,
        )
        editCard(
            navController = navController,
            showMessage = showMessage
        )
        editCategory(
            navController = navController,
            showMessage = showMessage
        )
        swipeGame(
            navController = navController,
            showMessage = showMessage
        )
    }
}
