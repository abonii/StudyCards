package abm.co.feature.card.editcategory

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import abm.co.designsystem.extensions.addPaddingOnShownKeyboard
import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditCategoryFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = EditCategoryFragment.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        EditCategoryPage(
            onBack = {
                findNavController().navigateUp()
            },
            navigateToNewCard = { categoryUI ->
                findNavController().navigate(
                    EditCategoryFragmentDirections.toEditCardNavGraph(
                        cardItem = null,
                        category = categoryUI
                    )
                )
            },
            showMessage = {
                messageContent(it)
            }
        )
    }
}