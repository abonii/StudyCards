package abm.co.feature.game.swipe

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import android.view.View
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SwipeGameFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = SwipeGameFragment.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        SwipeGamePage(
            onBack = {
                findNavController().navigateUp()
            },
            showMessage = messageContent
        )
    }
}