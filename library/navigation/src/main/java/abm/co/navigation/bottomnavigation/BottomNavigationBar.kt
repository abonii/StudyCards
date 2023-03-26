package abm.co.navigation.bottomnavigation

import abm.co.designsystem.component.composition.NoRippleTheme
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.navigation.R
import abm.co.navigation.navhost.card.graph.NewCardOrCategoryDestinations
import abm.co.navigation.navhost.root.Graph
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.height
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.collections.immutable.persistentListOf

private const val BOTTOM_BAR_HEIGHT = 84f // it is as dp

private val items = persistentListOf(
    BottomNavigationItem(
        nameRes = R.string.nav_home,
        route = Graph.HOME,
        iconRes = R.drawable.ic_home
    ),
    BottomNavigationItem(
        nameRes = R.string.nav_card,
        route = Graph.CARD,
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

@Composable
fun BottomNavigationBar(
    navigateToNewCardOrCategory: State<NewCardOrCategoryDestinations>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentScreenRoute = backStackEntry?.destination?.route
        val bottomNavVisible = remember(backStackEntry) {
            derivedStateOf {
                items.any {
                    currentScreenRoute == it.route &&
                        currentScreenRoute != Graph.NEW_CARD_OR_CATEGORY_GRAPH
                }
            }
        }
        val bottomBarHeightPx = remember { Animatable(0f) }
        LaunchedEffect(bottomNavVisible.value) {
            bottomBarHeightPx.animateTo(
                targetValue = if (bottomNavVisible.value) {
                    BOTTOM_BAR_HEIGHT
                } else {
                    0f
                }
            )
        }
        BottomNavigation(
            modifier = modifier.height(bottomBarHeightPx.value.dp),
            elevation = 5.dp,
            backgroundColor = StudyCardsTheme.colors.backgroundPrimary
        ) {
            items.forEach { item ->
                BottomNavigationIcon(
                    selected = item.route == currentScreenRoute,
                    item = item,
                    onClick = {
                        if (item.route == Graph.NEW_CARD_OR_CATEGORY_GRAPH) {
                            navController.navigate(
                                navigateToNewCardOrCategory.value.deepLink.toUri()
                            )
                        } else {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}