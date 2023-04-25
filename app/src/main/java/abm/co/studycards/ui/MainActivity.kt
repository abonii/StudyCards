package abm.co.studycards.ui

import abm.co.designsystem.extensions.launchLifecycleScope
import abm.co.studycards.R
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        setupSplash()
        setContentView(R.layout.activity_main)
        setupStartupDestination()
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
                    supportFragmentManager.findFragmentById(R.id.root_host_fragment) as NavHostFragment
                val inflater = navHostFragment.navController.navInflater
                val graph = inflater.inflate(it)

//                graph.setStartDestination(it)

                val navController = navHostFragment.navController
                navController.setGraph(graph, intent.extras)
            }
        }
    }
}
