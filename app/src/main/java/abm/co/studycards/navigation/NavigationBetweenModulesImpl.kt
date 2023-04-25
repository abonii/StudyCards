package abm.co.studycards.navigation

import abm.co.core.navigation.NavigationBetweenModules
import abm.co.studycards.R
import javax.inject.Inject

class NavigationBetweenModulesImpl @Inject constructor() : NavigationBetweenModules {
    override fun getNavigateFromAuthorizationToMain(): Int {
        return R.id.from_authorization_to_main
    }

    override fun getNavigateFromAuthorizationToUserPreferenceAndLanguage(): Int {
        return R.id.from_authorization_to_user_preference
    }

    override fun getNavigateFromUserPreferenceAndLanguageToMain(): Int {
        return R.id.from_user_preference_to_main
    }
}
