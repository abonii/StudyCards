package abm.co.navigation.navhost.card.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.navhost.card.category.category
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation

fun NavGraphBuilder.cardGraph(
    route: String,
    navController: NavController,
    showMessage: suspend (MessageContent) -> Unit,
    startDestination: String = CardDestinations.Category().route
) {
    navigation(
        route = route,
        startDestination = startDestination
    ) {
//        category(
//            navController = navController,
//            showMessage = showMessage,
//            bottomNavVisible = bottomNavVisible
//        )
    }
}

sealed class CardDestinations(val route: String) {
    object MainCard : CardDestinations("card_page")
    data class Category(
        val category: String = "category",
        val deepLink: String = "studycards://mobile/category_page"
    ) : CardDestinations("category_page")
}
