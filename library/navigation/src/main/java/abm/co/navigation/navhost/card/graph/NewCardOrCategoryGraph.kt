package abm.co.navigation.navhost.card.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.navhost.card.edit.editCard
import abm.co.navigation.navhost.card.edit.editCategory
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation

fun NavGraphBuilder.newCardOrSetGraph(
    route: String,
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit,
    startDestination: String = NewCardOrCategoryDestinations.Card.route
) {
    navigation(
        route = route,
        startDestination = startDestination,
    ) {
        editCard(
            navController = navController,
            showMessage = showMessage
        )
        editCategory(
            navController = navController,
            showMessage = showMessage
        )
    }
}

sealed class NewCardOrCategoryDestinations(val route: String, val deepLink: String) {
    object Card : NewCardOrCategoryDestinations(
        route = "edit_card",
        deepLink = "studycards://mobile/new_card"
    )

    object Category : NewCardOrCategoryDestinations(
        route = "edit_category",
        deepLink = "studycards://mobile/new_category"
    )
}

val LocalNewCardOrCategoryStartDestination =
    staticCompositionLocalOf<NewCardOrCategoryDestinations> {
        NewCardOrCategoryDestinations.Card
    }