package abm.co.navigation.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.authorization.signup.SignUpPage
import abm.co.navigation.Destinations
import abm.co.navigation.extensionfunction.navigate
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.signUp(
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit,
) {
    composable(
        route = Destinations.SignUp.route
    ) {
        SignUpPage(
            showMessage = showMessage,
            onNavigateChooseUserAttributes = {
                navController.navigate(
                    route = Destinations.ChooseUserAttributes().route,
                    args = bundleOf(
                        Destinations.ChooseUserAttributes().showAdditionQuiz to true
                    ),
                    navOptions = NavOptions.Builder().apply {
                        setPopUpTo(Destinations.SignUp.route, inclusive = true)
                    }.build()
                )
            },
            onNavigateLoginPage = {
                navController.navigate(Destinations.Login.route) {
                    popUpTo(Destinations.WelcomeLogin.route)
                }
            }
        )
    }
}
