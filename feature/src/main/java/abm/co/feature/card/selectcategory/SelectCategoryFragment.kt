package abm.co.feature.card.selectcategory

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import android.view.View
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectCategoryFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
        const val SELECT_CATEGORY_KEY = "SELECT_CATEGORY_KEY"
    }

    override val rootViewId: Int get() = SelectCategoryFragment.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        SelectCategoryPage(
            navigateBack = {
                if (it != null) {
                    setFragmentResult(
                        requestKey = SELECT_CATEGORY_KEY,
                        result = bundleOf(
                            "category" to it
                        )
                    )
                }
                findNavController().navigateUp()
            },
            showMessage = messageContent
        )
    }
}
