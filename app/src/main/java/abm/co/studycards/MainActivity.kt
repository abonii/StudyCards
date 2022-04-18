package abm.co.studycards

import abm.co.studycards.databinding.ActivityMainBinding
import abm.co.studycards.ui.login.LoginActivity
import abm.co.studycards.util.base.BaseBindingActivity
import abm.co.studycards.util.setupWithNavController
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : BaseBindingActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        checkIfLoggedIn()
    }

    override fun onResume() {
        super.onResume()
//        viewModel.verifySubscription()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.shutDownBillingClient()
    }

    override fun initViews(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
        lifecycleScope.launchWhenResumed {
            viewModel.toast.collectLatest {
                toast(this@MainActivity, it, true)
            }
        }
    }

    private fun checkLearnLanguagesSelected() {
        if (viewModel.isTargetAndSourceLangSet()) {
            viewModel.currentNavController?.value?.navigate(R.id.selectLanguageFragment)
        }
    }

    private fun checkIfLoggedIn() {
        if (viewModel.getCurrentUser() == null) {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        } else {
            checkLearnLanguagesSelected()
            viewModel.setDailyTranslateTime()
        }
    }

    override fun onBackPressed() {
        if (!viewModel.onBackAndNavigateUp())
            super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        return viewModel.onBackAndNavigateUp() || viewModel.currentNavController?.value!!.navigateUp()
                || super.onSupportNavigateUp()
    }

    private fun setupBottomNavigationBar() {
        val navGraphIds = listOf(
            R.navigation.home_tab,
            R.navigation.vocabulary_tab,
            R.navigation.explore_tab,
            R.navigation.profile_tab
        )
        val controller = binding.bottomNavView.setupWithNavController(
            navGraphIds = navGraphIds, fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container, intent = intent
        )

        viewModel.currentNavController = controller

        controller.observe(this) { navController ->
            navController.addOnDestinationChangedListener(listener)
        }

    }

    private val listener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.guessingFragment, R.id.confirmEndFragment, R.id.matchingPairsFragment,
                R.id.reviewFragment, R.id.selectLanguageFragment, R.id.toRightOrLeftFragment,
                -> {
                    slideDown()
                }
                else -> slideUp()
            }
        }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    fun setToolbar(toolbar: Toolbar, navController: NavController) {
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun slideDown() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        binding.bottomNavView.run {
            if (visibility == View.VISIBLE) {
                startAnimation(animation)
                visibility = View.GONE
            }
        }
    }

    private fun slideUp() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        binding.bottomNavView.run {
            if (visibility != View.VISIBLE) {
                startAnimation(animation)
                visibility = View.VISIBLE
            }
        }
    }

}

fun Activity.setDefaultStatusBar() {
    window.statusBarColor = resources.getColor(R.color.background, null)
}