package abm.co.studycards.navigation

import android.view.Menu
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions

object BottomNavigationUI {
    @JvmStatic
    fun onNavDestinationSelected(
        item: BottomNavigationItem,
        navController: NavController,
    ): Boolean {
        val builder = NavOptions.Builder().setLaunchSingleTop(true).setRestoreState(true)
        if (item.order and Menu.CATEGORY_SECONDARY == 0) {
            builder.setPopUpTo(
                navController.graph.findStartDestination().id,
                inclusive = false,
                saveState = true
            )
        }
        val options = builder.build()
        return try {
            navController.navigate(item.resId, null, options)
            navController.currentDestination?.matchDestination(item.resId) == true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    @JvmStatic
    private fun NavDestination.matchDestination(@IdRes destId: Int): Boolean =
        hierarchy.any { it.id == destId }
}
