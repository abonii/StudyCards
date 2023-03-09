package abm.co.navigation.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.domain.base.Failure
import abm.co.feature.signup.SignUpPage
import abm.co.navigation.Destinations
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.signUp(
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit,
) {
    composable(
        route = Destinations.Registration.route
    ) {
        SignUpPage(
            showMessage = showMessage,
            onNavigateHomePage = {
                navController.navigate(Destinations.Home.route)
            },
            onNavigateLoginPage = {
                navController.navigate(Destinations.Login.route)
            }
        )
    }
}
