package abm.co.studycards.navigation

import abm.co.designsystem.component.composition.NoRippleTheme
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.studycards.R
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.collections.immutable.persistentListOf

private const val BOTTOM_BAR_HEIGHT = 72f // it is as dp

private val bottomNavigationVisibleItemsId = persistentListOf(
    abm.co.feature.R.id.home_destination,
    abm.co.feature.R.id.main_card_destination,
    abm.co.feature.R.id.library_destination,
    abm.co.feature.R.id.profile_destination
)

val bottomNavigationItems = persistentListOf(
    BottomNavigationItem(
        nameRes = R.string.nav_home,
        resId = abm.co.feature.R.id.home_nav_graph,
        iconRes = R.drawable.ic_home,
        order = 0
    ),
    BottomNavigationItem(
        nameRes = R.string.nav_card,
        resId = abm.co.feature.R.id.main_card_nav_graph,
        iconRes = R.drawable.ic_card,
        order = 1
    ),
    BottomNavigationItem(
        nameRes = null,
        resId = abm.co.feature.R.id.new_category_nav_graph,
        iconRes = R.drawable.ic_new_card,
        order = 2
    ),
    BottomNavigationItem(
        nameRes = R.string.nav_library,
        resId = abm.co.feature.R.id.library_nav_graph,
        iconRes = R.drawable.ic_library,
        order = 3
    ),
    BottomNavigationItem(
        nameRes = R.string.nav_profile,
        resId = abm.co.feature.R.id.profile_nav_graph,
        iconRes = R.drawable.ic_user,
        order = 4
    )
)

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentScreenRoute = backStackEntry?.destination?.parent?.id
        val bottomNavVisible = remember {
            derivedStateOf {
                bottomNavigationVisibleItemsId.any {
                    backStackEntry?.destination?.id == it
                }
            }
        }
        val bottomBarHeight = remember { Animatable(0f) }
        LaunchedEffect(bottomNavVisible.value) {
            bottomBarHeight.animateTo(
                targetValue = if (bottomNavVisible.value) {
                    BOTTOM_BAR_HEIGHT
                } else {
                    0f
                }
            )
        }
        BottomNavigation(
            modifier = modifier
                .navigationBarsPadding()
                .height(bottomBarHeight.value.dp),
            elevation = 5.dp,
            backgroundColor = StudyCardsTheme.colors.backgroundPrimary
        ) {
            bottomNavigationItems.forEach { item ->
                BottomNavigationIcon(
                    modifier = Modifier.weight(1f),
                    selected = item.resId == currentScreenRoute,
                    item = item,
                    onClick = {
                        BottomNavigationUI.onNavDestinationSelected(
                            item,
                            navController
                        )
                    }
                )
            }
        }
    }
}
