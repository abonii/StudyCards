package abm.co.navigation

sealed class Destinations(val route: String) {
    object Home : Destinations("home_page")
    object Login : Destinations("login_page")
    object Registration : Destinations("registration_page")
    object Home2 : Destinations("home_page2")
    object Home3 : Destinations("home_page3")
}