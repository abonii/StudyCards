package abm.co.navigation.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.welcomelogin.WelcomeLoginPage
import abm.co.navigation.Destinations
import abm.co.navigation.extensionfunction.navigate
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.welcomeLogin(
    showMessage: suspend (MessageContent) -> Unit,
    navController: NavController
) {
    composable(
        route = Destinations.WelcomeLogin.route
    ) {
        WelcomeLoginPage(
            showMessage = showMessage,
            onNavigateChooseUserAttributes = {
                navController.navigate(
                    route = Destinations.ChooseUserAttributes().route,
                    args = bundleOf(
                        Destinations.ChooseUserAttributes().showAdditionQuiz to true
                    ),
                    navOptions = NavOptions.Builder().apply {
                        setPopUpTo(Destinations.WelcomeLogin.route, inclusive = true)
                    }.build()
                )
            },
            onNavigateSignUpPage = {
                navController.navigate(Destinations.SignUp.route)
            },
            onNavigateToLoginPage = {
                navController.navigate(Destinations.Login.route)
            }
        )
    }
}
