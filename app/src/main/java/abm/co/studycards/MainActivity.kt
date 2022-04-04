package abm.co.studycards

import abm.co.studycards.databinding.ActivityMainBinding
import abm.co.studycards.ui.login.LoginActivity
import abm.co.studycards.util.base.BaseBindingActivity
import abm.co.studycards.util.setupWithNavController
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseBindingActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        checkIfLoggedIn()
    }

    override fun initViews(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
    }

    private fun checkLearnLanguagesSelected() {
        if (viewModel.isTargetAndSourceLangSet()) {
            viewModel.currentNavController?.navigate(R.id.selectLanguageFragment)
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
        return viewModel.onBackAndNavigateUp() || viewModel.currentNavController!!.navigateUp()
                || super.onSupportNavigateUp()
    }

//    private fun checkIfIntentExtrasHas() {
//        if (intent.extras != null) {
//            val openProfile = intent.getBooleanExtra(
//                SHOULD_I_OPEN_PROFILE_FRAGMENT, false
//            )
//            if (!openProfile) {
//                binding.bottomNavView.selectedItemId = R.id.profile_tab
//                viewModel.currentNavController?.setGraph(R.navigation.profile_tab, intent.extras)
//            }
//        }
//    }


    private fun setupBottomNavigationBar() {
        //R.navigation.explore_tab,
        val navGraphIds = listOf(
            R.navigation.home_tab, R.navigation.vocabulary_tab, R.navigation.profile_tab
        )
        val controller = binding.bottomNavView.setupWithNavController(
            navGraphIds = navGraphIds, fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container, intent = intent
        )

        viewModel.currentNavController = controller?.apply {
            addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.guessingFragment, R.id.confirmEndFragment, R.id.matchingPairsFragment,
                    R.id.reviewFragment, R.id.selectLanguageFragment, R.id.toRightOrLeftFragment,
                    -> {
                        slideDown()
                    }
                    else -> slideUp()
                }
            }
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