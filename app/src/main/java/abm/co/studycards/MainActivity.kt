package abm.co.studycards

import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.databinding.ActivityMainBinding
import abm.co.studycards.ui.learn.confirmend.ConfirmText
import abm.co.studycards.ui.learn.guessing.GuessingFragmentDirections
import abm.co.studycards.ui.learn.matching.MatchingPairsFragmentDirections
import abm.co.studycards.ui.learn.review.ReviewFragmentDirections
import abm.co.studycards.ui.learn.rightleft.ToRightOrLeftFragmentDirections
import abm.co.studycards.ui.login.LoginActivity
import abm.co.studycards.util.Config
import abm.co.studycards.util.Constants
import abm.co.studycards.util.Constants.SHOULD_I_OPEN_PROFILE_FRAGMENT
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
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MainActivity : BaseBindingActivity<ActivityMainBinding>(R.layout.activity_main) {

    @Inject
    lateinit var prefs: Prefs

    @Inject
    @Named(Constants.USERS_REF)
    lateinit var userDbRef: DatabaseReference
    private lateinit var currentNavController: LiveData<NavController>

    override fun initViews(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
            checkIfIntentExtrasHas()
            checkIfLoggedIn()
            checkIfLearnLanguagesSelected()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Config.bottomNavHeight = binding.bottomNavView.height
    }

    private fun checkIfLearnLanguagesSelected() {
        if (prefs.getSourceLanguage().isBlank() || prefs.getTargetLanguage().isBlank()) {
            currentNavController.value?.navigate(R.id.selectLanguageFragment)
        }
    }

    private fun checkIfLoggedIn() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            lifecycleScope.launch {
                val currentCalendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }
                val yesterdayCalendar =
                    Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }
                val count = userDbRef.child("canTranslateTimeEveryDay")
                val time = userDbRef.child("canTranslateTimeInMills")
                if (count.get().await().value == null) {
                    count.setValue(20)
                }
                if (time.get().await().value == null) {
                    time.setValue(currentCalendar.timeInMillis)
                } else if (time.get()
                        .await().value as Long <= yesterdayCalendar.timeInMillis
                ) {
                    userDbRef.updateChildren(mapOf("canTranslateTimeInMills" to currentCalendar.timeInMillis))
                    userDbRef.updateChildren(mapOf("canTranslateTimeEveryDay" to 20))
                }
            }
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
                    SHOULD_I_OPEN_PROFILE_FRAGMENT, false
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
                R.id.confirmEndFragment,
                R.id.matchingPairsFragment,
                R.id.reviewFragment,
                R.id.selectLanguageFragment,
                R.id.toRightOrLeftFragment,
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