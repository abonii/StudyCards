package abm.co.navigation

sealed class Destinations(val route: String) {
    object Home : Destinations("home_page")
    object Login : Destinations("login_page")
    object SignUp : Destinations("sign_up_page")
    object WelcomeLogin : Destinations("welcome_login")
    object ChooseUserAttributes : Destinations("choose_user_attributes_login")
}