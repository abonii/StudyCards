package abm.co.navigation.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.authorization.login.LoginPage
import abm.co.feature.userattributes.ChooseUserAttributesPage
import abm.co.navigation.Destinations
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.chooseUserAttributes(
    showMessage: suspend (MessageContent) -> Unit,
    navController: NavController
) {
    composable(
        route = Destinations.ChooseUserAttributes.route
    ) {
        ChooseUserAttributesPage(
            onNavigateHomePage = {
                navController.navigate(
                    route = Destinations.Home.route
                )
            },
            showMessage = showMessage
        )
    }
}
