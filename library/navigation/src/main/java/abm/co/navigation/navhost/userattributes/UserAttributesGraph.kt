package abm.co.navigation.navhost.userattributes

import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.navhost.root.Graph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation

fun NavGraphBuilder.userAttributesNavGraph(
    navController: NavHostController,
    showMessage: suspend (MessageContent) -> Unit,
) {
    navigation(
        route = Graph.USER_ATTRIBUTES,
        startDestination = ChooseUserAttributesDestination().route
    ) {
        chooseUserAttributes(
            navController = navController,
            showMessage = showMessage
        )
    }
}

data class ChooseUserAttributesDestination(
    val showAdditionQuiz: String = "show_addition_quiz",
    val route: String = "choose_user_attributes_login"
)
