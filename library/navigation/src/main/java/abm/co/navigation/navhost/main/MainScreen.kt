package abm.co.navigation.navhost.main

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.domain.base.mapToFailure
import abm.co.navigation.R
import abm.co.navigation.bottomnavigation.BottomNavigationBar
import abm.co.navigation.bottomnavigation.BottomNavigationItem
import abm.co.navigation.navhost.main.graph.MainGraph
import abm.co.navigation.navhost.root.Graph
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    startDestination: String,
    showMessage: suspend (MessageContent) -> Unit
) {
    val navController: NavHostController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentScreenRoute = backStackEntry.value?.destination?.route
    val bottomNavVisible = remember(items, currentScreenRoute) {
        items.any { currentScreenRoute == it.route && currentScreenRoute != Graph.NEW_CARD_OR_CATEGORY_GRAPH }
    }
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = StudyCardsTheme.colors.backgroundPrimary,
        drawerGesturesEnabled = true,
        drawerShape = RoundedCornerShape(
            topStart = 0.dp,
            bottomStart = 0.dp,
            topEnd = 16.dp,
            bottomEnd = 16.dp
        ),
        drawerContent = { },
        bottomBar = {
            AnimatedVisibility(
                visible = bottomNavVisible,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                BottomNavigationBar(
                    items = items,
                    currentScreenRoute = currentScreenRoute,
                    navController = navController
                )
            }
        }
    ) { innerPaddings ->
        MainGraph(
            navController = navController,
            modifier = Modifier.padding(innerPaddings),
            startDestination = startDestination,
            showMessage = showMessage,
            openDrawer = {
                coroutineScope.launch {
                    try {
                        scaffoldState.drawerState.open()
                    } catch (e: CancellationException) {
                        e.mapToFailure().toMessageContent()?.let { showMessage(it) }
                    }
                }
            }
        )
    }
}

private val items = persistentListOf(
    BottomNavigationItem(
        nameRes = R.string.nav_home,
        route = Graph.HOME,
        iconRes = R.drawable.ic_home
    ),
    BottomNavigationItem(
        nameRes = R.string.nav_card,
        route = Graph.COLLECTION_OF_SET,
        iconRes = R.drawable.ic_card
    ),
    BottomNavigationItem(
        nameRes = null,
        route = Graph.NEW_CARD_OR_CATEGORY_GRAPH,
        iconRes = R.drawable.ic_new_card
    ),
    BottomNavigationItem(
        nameRes = R.string.nav_game,
        route = Graph.GAME,
        iconRes = R.drawable.ic_game
    ),
    BottomNavigationItem(
        nameRes = R.string.nav_profile,
        route = Graph.PROFILE,
        iconRes = R.drawable.ic_user
    )
)
