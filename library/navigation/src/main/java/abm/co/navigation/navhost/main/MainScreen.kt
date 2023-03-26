package abm.co.navigation.navhost.main

import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.domain.base.mapToFailure
import abm.co.navigation.bottomnavigation.BottomNavigationBar
import abm.co.navigation.navhost.card.graph.NewCardOrCategoryDestinations
import abm.co.navigation.navhost.main.graph.MainGraph
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    startDestination: String,
    navigateToNewCardOrCategory: State<NewCardOrCategoryDestinations>,
    showMessage: suspend (MessageContent) -> Unit,
) {
    val navController = rememberAnimatedNavController()
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = StudyCardsTheme.colors.backgroundPrimary,
        drawerGesturesEnabled = false,
        drawerShape = RoundedCornerShape(
            topStart = 0.dp,
            bottomStart = 0.dp,
            topEnd = 16.dp,
            bottomEnd = 16.dp
        ),
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                navigateToNewCardOrCategory = navigateToNewCardOrCategory
            )
        }
    ) { innerPaddings ->
        MainGraph(
            navController = navController,
            modifier = Modifier.padding(innerPaddings),
            startDestination = startDestination,
            showMessage = showMessage,
            openDrawer = {
                coroutineScope.launch {
                    try {
                        scaffoldState.drawerState.open()
                    } catch (e: CancellationException) {
                        e.mapToFailure().toMessageContent()?.let { showMessage(it) }
                    }
                }
            }
        )
    }
}
