package abm.co.feature.card.card

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import abm.co.designsystem.extensions.addPaddingOnShownKeyboard
import android.os.Bundle
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
                with(findNavController()) { navigateUp() }
            },
            showMessage = messageContent
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.addPaddingOnShownKeyboard(view)
    }
}