package abm.co.studycards.ui

import abm.co.studycards.R
import abm.co.studycards.navigation.BottomNavigationBar
import abm.co.studycards.navigation.BottomNavigationUI
import abm.co.studycards.navigation.bottomNavigationItems
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_main, container, false)
        setupBottomNavigation(view)
        return view
    }

    private fun setupBottomNavigation(view: View) {
        val navHostFragment = childFragmentManager.findFragmentById(
            R.id.main_host_fragment
        ) as NavHostFragment

        val navController = navHostFragment.navController

        val bottomNavigationHolder = view.findViewById<ComposeView>(R.id.bottom_navigation_view)
        BottomNavigationUI.onNavDestinationSelected(
            bottomNavigationItems[0],
            navController
        )
        bottomNavigationHolder.setContent {
            BottomNavigationBar(navController = navController)
        }
    }
}
