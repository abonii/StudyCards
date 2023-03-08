package abm.co.studycards.ui

import abm.co.navigation.BottomNavItem
import abm.co.navigation.Destinations
import abm.co.studycards.navigation.ComposeNewsNavHost
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.kaaveh.composenews.ui.component.BottomNavigationBar
import ir.kaaveh.designsystem.theme.ComposeNewsTheme
import okhttp3.internal.immutableListOf

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val items = immutableListOf(
        BottomNavItem(
            name = "Home",
            route = Destinations.Home.route,
            icon = Icons.Default.Home
        ),
        BottomNavItem(
            name = "Home 2",
            route = Destinations.Home2.route,
            icon = Icons.Default.Add
        ),
        BottomNavItem(
            name = "Home 3",
            route = Destinations.Home3.route,
            icon = Icons.Default.AccountBox
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            ComposeNewsTheme {
                val navController = rememberNavController()
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
                    }
                ) { innerPaddings ->
                    val viewModel: MainViewModel = hiltViewModel()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    ComposeNewsNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPaddings),
                        startDestination = if (state.isLoggedIn) Destinations.Home.route
                        else Destinations.WelcomeLogin.route,
                        onFailure = {

                        }
                    )
                }
            }
        }
    }
}
