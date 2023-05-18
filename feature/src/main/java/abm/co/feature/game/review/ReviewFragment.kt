package abm.co.feature.game.review

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
class ReviewFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = ReviewFragment.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        ReviewPage(
            navigateBack = {
                findNavController().navigateUp()
            },
            showMessage = messageContent
        )
    }
}
