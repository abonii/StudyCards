package abm.co.navigation.navhost.auth

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.authorization.signup.SignUpPage
import abm.co.navigation.extension.navigate
import abm.co.navigation.navhost.auth.graph.AuthDestinations
import abm.co.navigation.navhost.root.Graph
import abm.co.navigation.navhost.userattributes.ChooseUserAttributesDestination
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
        route = AuthDestinations.SignUp.route
    ) {
        SignUpPage(
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
            onNavigateLoginPage = {
                navController.navigate(AuthDestinations.Login.route) {
                    popUpTo(AuthDestinations.WelcomeLogin.route)
                }
            }
        )
    }
}
