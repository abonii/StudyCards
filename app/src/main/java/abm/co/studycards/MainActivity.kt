package abm.co.studycards

import abm.co.studycards.databinding.ActivityMainBinding
import abm.co.studycards.domain.model.ConfirmText
import abm.co.studycards.ui.login.LoginActivity
import abm.co.studycards.util.Constants.TAG_ERROR
import abm.co.studycards.util.base.BaseBindingActivity
import abm.co.studycards.util.setupWithNavController
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : BaseBindingActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels()
    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        checkIfLoggedIn()
    }

    override fun initViews(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            intent.getStringExtra(EXTRA_NAME)?.let { viewModel.setUserName(it) }
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
            currentNavController?.value?.navigate(R.id.selectLanguageFragment)
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
        if (!onBackAndNavigateUp())
            super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (!onBackAndNavigateUp())
                !currentNavController?.value!!.navigateUp()
            return true
        }
        return super.onOptionsItemSelected(item)
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

        currentNavController = controller

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
        try {
            super.onRestoreInstanceState(savedInstanceState)
            setupBottomNavigationBar()
        } catch (e: Exception) {
            Log.e(TAG_ERROR, "onRestoreInstanceState:${e.message}")
        }
    }

    fun setToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        toolbar.navigationIcon = getImg(R.drawable.ic_back)
    }

    private fun onBackAndNavigateUp(): Boolean {
        return when (currentNavController?.value?.currentDestination?.id) {
            R.id.guessingFragment,
            R.id.matchingPairsFragment,
            R.id.toRightOrLeftFragment,
            R.id.reviewFragment -> {
                currentNavController?.value?.navigate(
                    R.id.confirmEndFragment, bundleOf(Pair("confirmType", ConfirmText.ON_EXIT))
                )
                true
            }
            else -> false
        }
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

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    closeKeyboard(v)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun closeKeyboard(v: View) {
        val imm =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    companion object {
        const val EXTRA_NAME = "EXTRA_NAME"
    }

}

fun Activity.setDefaultStatusBar() {
    window.statusBarColor = resources.getColor(R.color.background, null)
}