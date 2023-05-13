package abm.co.core.navigation

import android.app.Activity
import androidx.navigation.NavController

interface NavigationBetweenModules {
    fun navigateFromAuthorizationToMain(navController: NavController)
    fun navigateFromUserPreferenceAndLanguageToMain(navController: NavController)
    fun navigateFromAuthorizationToUserPreferenceAndLanguage(
        navController: NavController,
        showAdditionQuiz: Boolean
    )
    fun navigateFromMainToAuthorization(activity: Activity)
}
