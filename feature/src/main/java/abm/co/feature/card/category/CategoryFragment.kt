package abm.co.feature.card.category

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import abm.co.designsystem.navigation.extension.getParcelableData
import abm.co.designsystem.navigation.extension.navigateSafe
import abm.co.feature.card.editcategory.EditCategoryFragment
import abm.co.feature.card.model.CategoryUI
import android.view.View
import androidx.compose.runtime.Composable
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = CategoryFragment.rootViewId

    private val viewModel by viewModels<CategoryViewModel>()

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
            navigateToChangeCategory = { category ->
                setFragmentResultListeners()
                findNavController().navigateSafe(
                    CategoryFragmentDirections.toEditCategoryDestination(
                        category = category
                    )
                )
            },
            viewModel = viewModel
        )
    }

    private fun setFragmentResultListeners() {
        setFragmentResultListener(EditCategoryFragment.EDIT_CATEGORY_KEY) { _, bundle ->
            val category = bundle.getParcelableData<CategoryUI>("category")
            category?.let { viewModel.onSelectedCategory(it) }
        }
    }
}