package abm.co.studycards.navigation

import abm.co.domain.base.Failure
import abm.co.navigation.graph.home
import abm.co.navigation.graph.home2
import abm.co.navigation.graph.home3
import abm.co.navigation.graph.login
import abm.co.navigation.graph.registration
import abm.co.navigation.graph.welcomeLogin
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun ComposeNewsNavHost(
    startDestination: String,
    navController: NavHostController,
    modifier: Modifier,
    onFailure: (Failure) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        welcomeLogin(
            navController = navController,
            onFailure = onFailure
        )
        login(
            navController = navController,
            onFailure = onFailure
        )
        registration(navController)
        home(navController)
        home2(navController)
        home3(navController)
    }
}