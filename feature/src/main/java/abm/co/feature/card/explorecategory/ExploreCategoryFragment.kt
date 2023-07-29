package abm.co.feature.card.explorecategory

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import android.view.View
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreCategoryFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = ExploreCategoryFragment.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        ExploreCategoryPage(
            navigateBack = {
                findNavController().navigateUp()
            },
            showMessage = messageContent
        )
    }
}