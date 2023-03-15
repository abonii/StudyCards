package abm.co.navigation.navhost.auth

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.welcomelogin.WelcomeLoginPage
import abm.co.navigation.graph.root.Graph
import abm.co.navigation.extension.navigate
import abm.co.navigation.graph.auth.AuthDestinations
import abm.co.navigation.graph.userattributes.ChooseUserAttributesDestination
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
        route = AuthDestinations.WelcomeLogin.route
    ) {
        WelcomeLoginPage(
            showMessage = showMessage,
            onNavigateChooseUserAttributes = {
                navController.navigate(
                    route = Graph.USER_ATTRIBUTES,
                    args = bundleOf(
                        ChooseUserAttributesDestination().showAdditionQuiz to true
                    ),
                    navOptions = NavOptions.Builder().apply {
                        setPopUpTo(Graph.AUTH, inclusive = true)
                    }.build()
                )
            },
            onNavigateSignUpPage = {
                navController.navigate(AuthDestinations.SignUp.route)
            },
            onNavigateToLoginPage = {
                navController.navigate(AuthDestinations.Login.route)
            }
        )
    }
}
