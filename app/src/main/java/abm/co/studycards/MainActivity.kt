package abm.co.studycards

import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.databinding.ActivityMainBinding
import abm.co.studycards.ui.home.HomeFragmentDirections
import abm.co.studycards.ui.profile.ProfileFragment
import abm.co.studycards.ui.sign.SignActivity
import abm.co.studycards.util.Config
import abm.co.studycards.util.base.BaseBindingActivity
import abm.co.studycards.util.setupWithNavController
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseBindingActivity<ActivityMainBinding>(R.layout.activity_main) {

    @Inject
    lateinit var prefs: Prefs
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
//            currentNavController.value?.popBackStack()
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


    private fun checkIfIntentExtrasHas() {
        if (intent.extras != null) {
            val openProfile = intent
                .getBooleanExtra(
                    ProfileFragment.SHOULD_I_OPEN_PROFILE_FRAGMENT, false
                )
            if (openProfile) {
                binding?.bottomNavView?.selectedItemId = R.id.profile_tab
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
        val controller = binding?.bottomNavView?.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

        if (controller != null) {
            currentNavController = controller
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController.value!!.navigateUp()
                || super.onSupportNavigateUp()
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
}