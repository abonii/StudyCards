package abm.co.navigation.navhost.card.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.navhost.card.edit.editCard
import abm.co.navigation.navhost.card.edit.editSetOfCards
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation

fun NavGraphBuilder.newCardOrSetGraph(
    navController: NavController,
    route: String,
    showMessage: suspend (MessageContent) -> Unit,
    startDestination: String = NewCardOrSetDestinations.Card.route
) {
    navigation(
        route = route,
        startDestination = startDestination
    ) {
        editCard(
            navController = navController,
            showMessage = showMessage
        )
        editSetOfCards(
            navController = navController,
            showMessage = showMessage
        )
    }
}

sealed class NewCardOrSetDestinations(val route: String, val deepLink: String? = null) {
    object Card : NewCardOrSetDestinations(
        route = "edit_card",
        deepLink = "studycards://mobile/new_card"
    )

    object SetOfCards : NewCardOrSetDestinations(
        route = "edit_set_of_cards",
        deepLink = "studycards://mobile/new_set_of_cards"
    )
}

val LocalNewCardOrSetStartDestination = staticCompositionLocalOf<NewCardOrSetDestinations> {
    NewCardOrSetDestinations.Card
}