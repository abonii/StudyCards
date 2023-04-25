package abm.co.core.navigation

interface NavigationBetweenModules {
    fun getNavigateFromAuthorizationToMain(): Int
    fun getNavigateFromAuthorizationToUserPreferenceAndLanguage(): Int
    fun getNavigateFromUserPreferenceAndLanguageToMain(): Int
}
