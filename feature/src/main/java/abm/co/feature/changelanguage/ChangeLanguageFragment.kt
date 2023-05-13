package abm.co.feature.changelanguage

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import android.view.View
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeLanguageFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = ChangeLanguageFragment.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        ChangeLanguagePage(
            showMessage = messageContent,
            navigateBack = {
                findNavController().navigateUp()
            }
        )
    }
}