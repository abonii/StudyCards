package abm.co.navigation.bottomnavigation

import abm.co.designsystem.component.composition.NoRippleTheme
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.navigation.navhost.card.graph.LocalNewCardOrCategoryStartDestination
import abm.co.navigation.navhost.card.graph.NewCardOrCategoryDestinations
import abm.co.navigation.navhost.root.Graph
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import kotlinx.collections.immutable.ImmutableList

@Composable
fun BottomNavigationBar(
    items: ImmutableList<BottomNavigationItem>,
    navigateToNewCardOrCategory: State<NewCardOrCategoryDestinations>,
    currentScreenRoute: String?,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
        BottomNavigation(
            modifier = modifier,
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