package abm.co.feature.card.card

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import android.view.View
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditCardFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = EditCardFragment.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        EditCardPage(
            onBack = {
                findNavController().navigateUp()
            },
            showMessage = messageContent
        )
    }
}