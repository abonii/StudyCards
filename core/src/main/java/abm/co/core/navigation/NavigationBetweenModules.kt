package abm.co.core.navigation

import androidx.navigation.NavController

interface NavigationBetweenModules {
    fun navigateFromAuthorizationToMain(navController: NavController)
    fun navigateFromUserPreferenceAndLanguageToMain(navController: NavController)
    fun navigateFromAuthorizationToUserPreferenceAndLanguage(
        navController: NavController,
        showAdditionQuiz: Boolean
    )
}
