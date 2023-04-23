package abm.co.studycards.navigation

import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions

/**
 * Class which hooks up elements typically in the 'chrome' of your application such as global
 * navigation patterns like a navigation drawer or bottom nav bar with your [NavController].
 */
object BottomNavigationUI{
    /**
     * Attempt to navigate to the [NavDestination] associated with the given MenuItem. This
     * MenuItem should have been added via one of the helper methods in this class.
     *
     * Importantly, it assumes the [menu item id][MenuItem.getItemId] matches a valid
     * [action id][NavDestination.getAction] or [destination id][NavDestination.id] to be
     * navigated to.
     *
     * By default, the back stack will be popped back to the navigation graph's start destination.
     * Menu items that have `android:menuCategory="secondary"` will not pop the back
     * stack.
     *
     * @param item The selected MenuItem.
     * @param navController The NavController that hosts the destination.
     * @return True if the [NavController] was able to navigate to the destination
     * associated with the given MenuItem.
     */
    @JvmStatic
    fun onNavDestinationSelected(
        item: BottomNavigationItem,
        navController: NavController
    ): Boolean {
        val builder = NavOptions.Builder().setLaunchSingleTop(true).setRestoreState(true)
        if (item.order == 0) {
            builder.setPopUpTo(
                navController.graph.findStartDestination().id,
                inclusive = false,
                saveState = true
            )
        }
        val options = builder.build()
        return try {
            // TODO provide proper API instead of using Exceptions as Control-Flow.
            navController.navigate(item.resId, null, options)
            // Return true only if the destination we've navigated to matches the BottomNavigationItem
            navController.currentDestination?.matchDestination(item.resId) == true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    /**
     * Determines whether the given `destId` matches the NavDestination. This handles
     * both the default case (the destination's id matches the given id) and the nested case where
     * the given id is a parent/grandparent/etc of the destination.
     */
    @JvmStatic
    internal fun NavDestination.matchDestination(@IdRes destId: Int): Boolean =
        hierarchy.any { it.id == destId }
}
