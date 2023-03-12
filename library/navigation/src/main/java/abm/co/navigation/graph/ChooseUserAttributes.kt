package abm.co.navigation.graph

import abm.co.designsystem.message.common.MessageContent
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
        route = Destinations.ChooseUserAttributes().route
    ) { entry ->
        val showAdditionQuiz = entry.arguments?.getBoolean(
            Destinations.ChooseUserAttributes().showAdditionQuiz
        ) ?: true
        ChooseUserAttributesPage(
            showAdditionQuiz = showAdditionQuiz,
            onNavigateHomePage = {
                navController.navigate(Destinations.Home.route) {
                    popUpTo(Destinations.ChooseUserAttributes().route) {
                        inclusive = true
                    }
                }
            },
            showMessage = showMessage
        )
    }
}
