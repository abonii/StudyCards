package abm.co.feature.card.editcategory

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
class EditCategoryFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
        const val EDIT_CATEGORY_KEY = "EDIT_CATEGORY_KEY"
    }

    override val rootViewId: Int get() = EditCategoryFragment.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        EditCategoryPage(
            showMessage = messageContent,
            navigateBack = {
                it?.let {
                    setFragmentResult(
                        requestKey = EDIT_CATEGORY_KEY,
                        result = bundleOf(
                            "category" to it
                        )
                    )
                }
                findNavController().navigateUp()
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.addPaddingOnShownKeyboard(view)
    }

}
