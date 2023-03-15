package abm.co.navigation.graph.card

import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.navhost.card.collectionOfSet
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CollectionOfSetGraph(
    route: String,
    showMessage: suspend (MessageContent) -> Unit,
    startDestination: String = CardDestinations.CollectionOfSet.route
) {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(
        route = route,
        navController = navController,
        startDestination = startDestination
    ) {
        collectionOfSet(
            navController = navController,
            showMessage = showMessage
        )
    }
}

sealed class CardDestinations(val route: String) {
    object CollectionOfSet : CardDestinations("collection_of_set")
}
