package abm.co.navigation.navhost.auth.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.navhost.auth.login
import abm.co.navigation.navhost.auth.signUp
import abm.co.navigation.navhost.auth.welcomeLogin
import abm.co.navigation.navhost.root.Graph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    showMessage: suspend (MessageContent) -> Unit,
) {
    navigation(
        route = Graph.AUTH,
        startDestination = AuthDestinations.WelcomeLogin.route
    ) {
        welcomeLogin(
            navController = navController,
            showMessage = showMessage
        )
        login(
            navController = navController,
            showMessage = showMessage
        )
        signUp(
            navController = navController,
            showMessage = showMessage
        )
    }
}

sealed class AuthDestinations(val route: String) {
    object Login : AuthDestinations("login_page")
    object SignUp : AuthDestinations("sign_up_page")
    object WelcomeLogin : AuthDestinations("welcome_login")
}
