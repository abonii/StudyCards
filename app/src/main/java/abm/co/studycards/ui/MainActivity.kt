package abm.co.studycards.ui

import abm.co.designsystem.extensions.launchLifecycleScope
import abm.co.studycards.R
import abm.co.studycards.navigation.BottomNavigationBar
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapNotNull

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        setupSplash()
        setContentView(R.layout.activity_main)
        setupStartupDestination()
        setupBottomNavigation()
    }

    private fun setFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun setupSplash() {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.state.value.isSplashScreenVisible
            }
        }
    }

    private fun setupStartupDestination() {
        launchLifecycleScope {
            viewModel.startDestination.collectLatest {
                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.main_host_fragment) as NavHostFragment
                val inflater = navHostFragment.navController.navInflater
                val graph = inflater.inflate(R.navigation.root_nav_graph)

                graph.setStartDestination(it)

                val navController = navHostFragment.navController
                navController.setGraph(graph, intent.extras)
            }
        }
    }

    private fun setupBottomNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.main_host_fragment
        ) as NavHostFragment

        val navController = navHostFragment.navController

        val bottomNavigationHolder = findViewById<ComposeView>(R.id.bottom_navigation_view)
        bottomNavigationHolder.setContent {
            BottomNavigationBar(
                navController = navController
            )
        }
    }
}
