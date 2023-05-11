package abm.co.feature.card.chooseorcreatecategory

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import abm.co.designsystem.extensions.addPaddingOnShownKeyboard
import abm.co.designsystem.navigation.extension.navigateSafe
import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseOrCreateCategoryFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = ChooseOrCreateCategoryFragment.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        ChooseOrCreateCategoryPage(
            onBack = {
                findNavController().navigateUp()
            },
            navigateToNewCard = { categoryUI ->
                findNavController().navigateSafe(
                    ChooseOrCreateCategoryFragmentDirections.toEditCardNavGraph(
                        card = null,
                        category = categoryUI
                    )
                )
            },
            showMessage = {
                messageContent(it)
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.addPaddingOnShownKeyboard(view)
    }
}