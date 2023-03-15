package abm.co.navigation.graph.main

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.navigation.R
import abm.co.navigation.bottomnavigation.BottomNavigationBar
import abm.co.navigation.bottomnavigation.BottomNavigationItem
import abm.co.navigation.graph.root.Graph
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    startDestination: String,
    showMessage: suspend (MessageContent) -> Unit,
    navController: NavHostController = rememberAnimatedNavController()
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentScreenRoute = backStackEntry.value?.destination?.route
    val bottomNavVisible = remember(items, currentScreenRoute) {
        items.any { currentScreenRoute != Graph.NEW_CARD_OR_SET_GRAPH }
    }
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = StudyCardsTheme.colors.backgroundPrimary,
        drawerGesturesEnabled = false,
        drawerElevation = 0.dp,
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
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                BottomNavigationBar(
                    items = items,
                    currentScreenRoute = currentScreenRoute,
                    navController = navController
                )
            }
        },
    ) { innerPaddings ->
        MainGraph(
            navController = navController,
            modifier = Modifier.padding(innerPaddings),
            startDestination = startDestination,
            showMessage = showMessage
        )
    }
}

fun customShape() = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rectangle(Rect(0f, 0f, 100f /* width */, 131f /* height */))
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
        route = Graph.NEW_CARD_OR_SET_GRAPH,
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