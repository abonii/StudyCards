package abm.co.studycards.ui

import abm.co.designsystem.message.alert.MessageAlertDialog
import abm.co.designsystem.message.common.MessageAlertContent
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.message.common.toMessageContent
import abm.co.designsystem.message.snackbar.MessageSnackbar
import abm.co.designsystem.message.snackbar.showSnackbarWithContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.navigation.BottomNavItem
import abm.co.navigation.Destinations
import abm.co.studycards.navigation.ComposeNewsNavHost
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.kaaveh.composenews.ui.component.BottomNavigationBar
import okhttp3.internal.immutableListOf

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val items = immutableListOf(
        BottomNavItem(
            name = "Home",
            route = Destinations.Home.route,
            icon = Icons.Default.Home
        )
    )

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen()
        setContent {
            StudyCardsTheme {
                val navController = rememberAnimatedNavController()
                val backStackEntry = navController.currentBackStackEntryAsState()
                val currentScreenRoute = backStackEntry.value?.destination?.route
                val bottomNavVisible = remember(items, currentScreenRoute) {
                    items.any {
                        it.route == currentScreenRoute
                    }
                }
                Scaffold(
                    bottomBar = {
                        AnimatedVisibility(
                            visible = bottomNavVisible,
                            enter = slideInVertically { it },
                            exit = slideOutVertically { it },
                        ) {
                            BottomNavigationBar(
                                items = items,
                                currentScreenRoute = currentScreenRoute
                            ) {
                                navController.navigate(it.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    },
                    backgroundColor = StudyCardsTheme.colors.backgroundPrimary
                ) { innerPaddings ->
                    val snackbarHostState = remember { SnackbarHostState() }
                    var showAlertDialog by remember { mutableStateOf<MessageAlertContent?>(null) }
                    val viewModel: MainViewModel = hiltViewModel()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    ComposeNewsNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPaddings),
                        startDestination = if (state.isLoggedIn) Destinations.Home.route
                        else Destinations.WelcomeLogin.route,
                        showMessage = {
                            when (it) {
                                is MessageContent.AlertDialog -> {
                                    showAlertDialog = it.toMessageContent(this)
                                }
                                is MessageContent.Snackbar -> {
                                    snackbarHostState.showSnackbarWithContent(
                                        it.toMessageContent(this)
                                    )
                                }
                            }
                        }
                    )
                    MessageAlertDialog(
                        showAlertDialog = showAlertDialog,
                        onDismiss = { showAlertDialog = null },
                    )
                    MessageSnackbar(snackbarHostState = snackbarHostState)
                }
            }
        }
    }
}
