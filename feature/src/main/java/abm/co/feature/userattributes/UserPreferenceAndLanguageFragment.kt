package abm.co.feature.userattributes

import abm.co.core.navigation.NavigationBetweenModules
import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import android.view.View
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserPreferenceAndLanguageFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = UserPreferenceAndLanguageFragment.rootViewId

    @Inject
    lateinit var navigationBetweenModules: NavigationBetweenModules

    @Composable
    override fun InitUI(messageContent: messageContent) {
        UserPreferenceAndLanguage(
            onNavigateHomePage = {
                navigationBetweenModules.navigateFromUserPreferenceAndLanguageToMain(
                    findNavController()
                )
            },
            showMessage = messageContent
        )
    }
}
