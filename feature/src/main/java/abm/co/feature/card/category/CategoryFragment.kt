package abm.co.feature.card.category

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import abm.co.designsystem.navigation.extension.navigateSafe
import android.view.View
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = CategoryFragment.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        CategoryPage(
            showMessage = {
                messageContent(it)
            },
            onBack = {
                findNavController().navigateUp()
            },
            navigateToCard = { cardItem, category ->
                findNavController().navigateSafe(
                    CategoryFragmentDirections.toEditCardNavGraph(
                        card = cardItem,
                        category = category
                    )
                )
            },
            navigateToChooseOrCreateCategory = {

            }
        )
    }
}