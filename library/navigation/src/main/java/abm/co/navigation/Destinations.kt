package abm.co.navigation

sealed class Destinations(val route: String) {
    object Home : Destinations("home_page")
    object Login : Destinations("login_page")
    object SignUp : Destinations("sign_up_page")
    object WelcomeLogin : Destinations("welcome_login")
    data class ChooseUserAttributes(val showAdditionQuiz: String = "show_addition_quiz") :
        Destinations("choose_user_attributes_login")
}