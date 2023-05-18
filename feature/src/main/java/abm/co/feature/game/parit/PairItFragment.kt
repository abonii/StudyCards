package abm.co.feature.game.parit

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import abm.co.designsystem.extensions.addPaddingOnShownKeyboard
import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PairItFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = PairItFragment.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        PairItPage(
            navigateBack = {
                findNavController().navigateUp()
            },
            showMessage = messageContent
        )
    }
}
