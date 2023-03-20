package abm.co.navigation.navhost.card.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.navhost.card.edit.editCard
import abm.co.navigation.navhost.card.edit.editCategory
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NewCardOrSetGraph(
    route: String,
    showMessage: suspend (MessageContent) -> Unit,
    startDestination: String = NewCardOrCategoryDestinations.Card.route
) {
    val navController = rememberAnimatedNavController()
    val startDestinationNewCardOrCategory = LocalNewCardOrCategoryStartDestination.current
    AnimatedNavHost(
        route = route,
        navController = navController,
        startDestination = startDestinationNewCardOrCategory.route,
        enterTransition = { fadeIn(initialAlpha = 1f) },
        exitTransition = { fadeOut(targetAlpha = 1f) },
        popEnterTransition = { fadeIn(initialAlpha = 1f) },
        popExitTransition = { fadeOut(targetAlpha = 1f) }
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

sealed class NewCardOrCategoryDestinations(val route: String, val deepLink: String? = null) {
    object Card : NewCardOrCategoryDestinations(
        route = "edit_card",
        deepLink = "studycards://mobile/new_card"
    )

    object Category : NewCardOrCategoryDestinations(
        route = "edit_category",
        deepLink = "studycards://mobile/new_category"
    )

    companion object {
        fun getAllDeepLinks(): List<String> {
            return listOfNotNull(Card.deepLink, Category.deepLink)
        }
    }
}

val LocalNewCardOrCategoryStartDestination =
    staticCompositionLocalOf<NewCardOrCategoryDestinations> {
        NewCardOrCategoryDestinations.Card
    }