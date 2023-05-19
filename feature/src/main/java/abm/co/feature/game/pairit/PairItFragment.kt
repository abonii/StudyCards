package abm.co.feature.game.pairit

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import abm.co.feature.game.repeat.RepeatFragment
import abm.co.feature.game.review.ReviewFragment
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
        const val PAIR_IT_FINISHED_KEY = "PAIR_IT_FINISHED_KEY"
    }

    override val rootViewId: Int get() = PairItFragment.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        PairItPage(
            nextPageAfterFinish = {
                requireParentFragment().setFragmentResult(
                    PAIR_IT_FINISHED_KEY,
                    bundleOf()
                )
            },
            navigateBack = { isRepeat ->
                if(isRepeat) {
                    requireParentFragment().setFragmentResult(
                        RepeatFragment.BACK_PRESSED_KEY,
                        bundleOf()
                    )
                } else {
                    findNavController().navigateUp()
                }
            },
            showMessage = messageContent
        )
    }
}
