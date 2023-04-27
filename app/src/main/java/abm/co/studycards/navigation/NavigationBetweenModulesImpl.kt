package abm.co.studycards.navigation

import abm.co.core.navigation.NavigationBetweenModules
import abm.co.designsystem.navigation.extension.navigateSafe
import abm.co.studycards.R
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import javax.inject.Inject

class NavigationBetweenModulesImpl @Inject constructor() : NavigationBetweenModules {

    companion object {
        val builder = NavOptions.Builder()
            .setEnterAnim(abm.co.designsystem.R.anim.enter_from_right)
            .setExitAnim(abm.co.designsystem.R.anim.exit_to_left)
            .setPopEnterAnim(abm.co.designsystem.R.anim.pop_enter_from_left)
            .setPopExitAnim(abm.co.designsystem.R.anim.pop_exit_to_right)
    }

    override fun navigateFromAuthorizationToMain(navController: NavController) {
        val navOptions = builder
            .setPopUpTo(abm.co.feature.R.id.authorization_nav_graph, true)
            .build()
        navController.navigateSafe(R.id.root_main_nav_graph, null, navOptions)
    }

    override fun navigateFromAuthorizationToUserPreferenceAndLanguage(navController: NavController) {
        val navOptions = builder
            .setPopUpTo(abm.co.feature.R.id.authorization_nav_graph, true)
            .build()
        navController.navigateSafe(
            R.id.root_user_preference_and_language_nav_graph,
            null,
            navOptions
        )
    }

    override fun navigateFromUserPreferenceAndLanguageToMain(navController: NavController) {
        val navOptions = builder
            .setPopUpTo(R.id.root_user_preference_and_language_nav_graph, true)
            .build()
        navController.navigateSafe(R.id.root_main_nav_graph, null, navOptions)
    }
}
