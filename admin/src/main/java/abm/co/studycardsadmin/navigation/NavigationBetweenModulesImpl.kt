package abm.co.studycardsadmin.navigation

import abm.co.core.navigation.NavigationBetweenModules
import abm.co.studycardsadmin.MainActivity
import android.app.Activity
import android.content.Intent
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
    }

    override fun navigateFromAuthorizationToUserPreferenceAndLanguage(
        navController: NavController,
        showAdditionQuiz: Boolean
    ) {
    }

    override fun navigateFromUserPreferenceAndLanguageToMain(navController: NavController) {
    }

    override fun navigateFromMainToAuthorization(activity: Activity) {
        val intent = Intent(activity, MainActivity::class.java)
        activity.startActivity(intent)
    }
}
