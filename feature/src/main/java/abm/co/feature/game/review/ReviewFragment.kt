package abm.co.feature.game.review

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import abm.co.feature.game.repeat.GameHolderFragment
import abm.co.feature.game.repeat.GameHolderFragment.Companion.BACK_PRESSED_KEY
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
        const val REVIEW_FINISHED_KEY = "REVIEW_FINISHED_KEY"
    }

    override val rootViewId: Int get() = ReviewFragment.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        ReviewPage(
            showMessage = messageContent,
            nextPageAfterFinish = {
                requireParentFragment().setFragmentResult(
                    REVIEW_FINISHED_KEY,
                    bundleOf()
                )
            },
            navigateBack = { isRepeat ->
                if(isRepeat) {
                    requireParentFragment().setFragmentResult(
                        BACK_PRESSED_KEY,
                        bundleOf()
                    )
                } else {
                    findNavController().navigateUp()
                }
            },
            onProgressChanged = {
                requireParentFragment().setFragmentResult(
                    GameHolderFragment.PROGRESS_KEY,
                    bundleOf(
                        "progress" to it
                    )
                )
            }
        )
    }
}
