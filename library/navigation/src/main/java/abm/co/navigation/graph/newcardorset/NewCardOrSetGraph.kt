package abm.co.navigation.graph.newcardorset

import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.navhost.card.newCardOrSet
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NewCardOrSetGraph(
    route: String,
    showMessage: suspend (MessageContent) -> Unit,
    startDestination: String = NewCardOrSetDestinations.NewCardOrSet.route
) {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(
        route = route,
        navController = navController,
        startDestination = startDestination
    ) {
        newCardOrSet(
            navController = navController,
            showMessage = showMessage
        )
    }
}

sealed class NewCardOrSetDestinations(val route: String) {
    object NewCardOrSet : NewCardOrSetDestinations("new_card_or_set")
}

