package abm.co.navigation.navhost.userattributes

import abm.co.designsystem.message.common.MessageContent
import abm.co.feature.userattributes.ChooseUserAttributesPage
import abm.co.navigation.graph.root.Graph
import abm.co.navigation.graph.userattributes.ChooseUserAttributesDestination
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
        route = ChooseUserAttributesDestination().route
    ) {
        ChooseUserAttributesPage(
            onNavigateHomePage = {
                navController.navigate(Graph.MAIN) {
                    popUpTo(Graph.USER_ATTRIBUTES) { inclusive = true }
                }
            },
            showMessage = showMessage
        )
    }
}
