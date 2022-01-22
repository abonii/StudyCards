package abm.co.studycards

import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.databinding.ActivityMainBinding
import abm.co.studycards.ui.home.HomeFragmentDirections
import abm.co.studycards.ui.learn.confirmend.ConfirmText
import abm.co.studycards.ui.learn.guessing.GuessingFragmentDirections
import abm.co.studycards.ui.learn.matching.MatchingPairsFragmentDirections
import abm.co.studycards.ui.learn.review.ReviewFragmentDirections
import abm.co.studycards.ui.learn.rightleft.ToRightOrLeftFragmentDirections
import abm.co.studycards.ui.profile.ProfileFragment
import abm.co.studycards.ui.sign.SignActivity
import abm.co.studycards.util.Config
import abm.co.studycards.util.Constants
import abm.co.studycards.util.base.BaseBindingActivity
import abm.co.studycards.util.setupWithNavController
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MainActivity : BaseBindingActivity<ActivityMainBinding>(R.layout.activity_main) {

    @Inject
    lateinit var prefs: Prefs

    @Inject
    @Named(Constants.USERS_REF)
    lateinit var categoriesDbRef: DatabaseReference
    private lateinit var currentNavController: LiveData<NavController>

    override fun initViews(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
            checkIfIntentExtrasHas()
            checkIfLoggedIn()
            checkIfLearnLanguagesSelected()
        }
        lifecycleScope.launch {
            if (categoriesDbRef.child("canTranslateTimeEveryDay").get().await().value == null)
                categoriesDbRef.setValue(mapOf("canTranslateTimeEveryDay" to 20))
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Config.bottomNavHeight = binding.bottomNavView.height
    }

    private fun checkIfLearnLanguagesSelected() {
        if (prefs.getSourceLanguage().isBlank() || prefs.getTargetLanguage().isBlank()) {
            currentNavController.value?.navigate(HomeFragmentDirections.actionHomeFragmentToSelectLanguageFragment())
        }
    }

    private fun checkIfLoggedIn() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            val intent = Intent(this, SignActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        onBackAndNavigateUp()
    }

    private fun onBackAndNavigateUp(): Boolean {
        val navController = currentNavController.value
        return when (navController?.currentDestination?.id) {
            R.id.guessingFragment -> {
                navController.navigate(
                    GuessingFragmentDirections
                        .actionGlobalConfirmEndFragment(ConfirmText.ON_EXIT)
                )
                true
            }
            R.id.matchingPairsFragment -> {
                navController.navigate(
                    MatchingPairsFragmentDirections
                        .actionGlobalConfirmEndFragment(ConfirmText.ON_EXIT)
                )
                true
            }
            R.id.toRightOrLeftFragment -> {
                navController.navigate(
                    ToRightOrLeftFragmentDirections
                        .actionGlobalConfirmEndFragment(ConfirmText.ON_EXIT)
                )
                true
            }
            R.id.reviewFragment -> {
                navController.navigate(
                    ReviewFragmentDirections
                        .actionGlobalConfirmEndFragment(ConfirmText.ON_EXIT)
                )
                true
            }
            else -> {
                super.onBackPressed()
                return true
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController.value!!.navigateUp()
                || super.onSupportNavigateUp()
    }


    private fun checkIfIntentExtrasHas() {
        if (intent.extras != null) {
            val openProfile = intent
                .getBooleanExtra(
                    ProfileFragment.SHOULD_I_OPEN_PROFILE_FRAGMENT, false
                )
            if (openProfile) {
                binding.bottomNavView.selectedItemId = R.id.profile_tab
                currentNavController.value?.setGraph(R.navigation.profile_tab, intent.extras)
            }
        }
    }

    private fun setupBottomNavigationBar() {
        val navGraphIds = listOf(
            R.navigation.home_tab,
            R.navigation.vocabulary_tab,
//            R.navigation.explore_tab,
            R.navigation.profile_tab
        )
        val controller = binding.bottomNavView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

        currentNavController = controller
        controller.value?.addOnDestinationChangedListener { it, destination, _ ->
            if (it.previousBackStackEntry?.destination?.id == R.id.homeFragment &&
                destination.id == R.id.addEditCategoryFragment
            ) {
                return@addOnDestinationChangedListener
            }
            when (destination.id) {
                R.id.guessingFragment,
                R.id.matchingPairsFragment,
                R.id.reviewFragment,
                R.id.toRightOrLeftFragment
                -> {
                    slideDown()

                }
                else -> slideUp()
            }
        }
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    fun setToolbar(toolbar: Toolbar?, navController: NavController) {
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun slideDown(v: Int = View.GONE) {
        if (binding.bottomNavView.visibility == View.VISIBLE) {
            val animation = AnimationUtils.loadAnimation(this, R.anim.slide_down)
            binding.bottomNavView.startAnimation(animation)
            binding.bottomNavView.visibility = v
        }
    }

    private fun slideUp() {
        if (binding.bottomNavView.visibility != View.VISIBLE) {
            val animation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
            binding.bottomNavView.startAnimation(animation)
            binding.bottomNavView.visibility = View.VISIBLE
        }
    }

}