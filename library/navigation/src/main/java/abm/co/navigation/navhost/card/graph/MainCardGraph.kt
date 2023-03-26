package abm.co.navigation.navhost.card.graph

import abm.co.designsystem.message.common.MessageContent
import abm.co.navigation.navhost.card.main.mainCard
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainCardGraph(
    route: String,
    showMessage: suspend (MessageContent) -> Unit,
    startDestination: String = CardDestinations.MainCard.route
) {
//    val navController = rememberAnimatedNavController()
//    AnimatedNavHost(
//        route = route,
//        navController = navController,
//        startDestination = startDestination
//    ) {
//        mainCard(
//            navController = navController,
//            showMessage = showMessage
//        )
//    }
}

sealed class CardDestinations(val route: String) {
    object MainCard : CardDestinations("card_page")
    object Category : CardDestinations("category_page")
}
