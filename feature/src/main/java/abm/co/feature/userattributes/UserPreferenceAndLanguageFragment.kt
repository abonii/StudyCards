package abm.co.feature.userattributes

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import abm.co.feature.R
import android.view.View
import androidx.compose.runtime.Composable
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserPreferenceAndLanguageFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = UserPreferenceAndLanguageFragment.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        UserPreferenceAndLanguage(
            onNavigateHomePage = {
                val navController = findNavController()
                val builder = NavOptions.Builder().setLaunchSingleTop(true).setRestoreState(true)
                if (
                    navController.currentDestination!!.parent!!.findNode(R.id.home_nav_graph)
                            is ActivityNavigator.Destination
                ) {
                    builder.setEnterAnim(abm.co.designsystem.R.anim.enter_from_right)
                        .setExitAnim(abm.co.designsystem.R.anim.exit_to_left)
                        .setPopEnterAnim(abm.co.designsystem.R.anim.pop_enter_from_left)
                        .setPopExitAnim(abm.co.designsystem.R.anim.pop_exit_to_right)
                } else {
                    builder.setEnterAnim(abm.co.designsystem.R.anim.enter_from_right)
                        .setExitAnim(abm.co.designsystem.R.anim.exit_to_left)
                        .setPopEnterAnim(abm.co.designsystem.R.anim.pop_enter_from_left)
                        .setPopExitAnim(abm.co.designsystem.R.anim.pop_exit_to_right)
                }
                builder.setPopUpTo(
                    R.id.user_preference_and_language_nav_graph,
                    inclusive = false,
                    saveState = true
                )
                val options = builder.build()
                navController.navigate(R.id.home_nav_graph, null, options)
            },
            showMessage = messageContent
        )
    }
}