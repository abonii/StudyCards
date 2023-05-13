package abm.co.feature.card.card

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import abm.co.designsystem.extensions.addPaddingOnShownKeyboard
import abm.co.designsystem.navigation.extension.getParcelableData
import abm.co.designsystem.navigation.extension.navigateSafe
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.card.selectcategory.SelectCategoryFragment
import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditCardFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = EditCardFragment.rootViewId

    val viewModel by viewModels<EditCardViewModel>()

    @Composable
    override fun InitUI(messageContent: messageContent) {
        EditCardPage(
            showMessage = messageContent,
            onBack = {
                with(findNavController()) { navigateUp() }
            },
            onClickChangeCategory = {
                setFragmentResultListeners()
                findNavController().navigateSafe(
                    EditCardFragmentDirections.toSelectCategoryDestination(
                        categoryId = it
                    )
                )
            },
            viewModel = viewModel
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.addPaddingOnShownKeyboard(view)
    }

    private fun setFragmentResultListeners() {
        setFragmentResultListener(SelectCategoryFragment.SELECT_CATEGORY_KEY) { _, bundle ->
            val category = bundle.getParcelableData<CategoryUI>("category")
            category?.let { viewModel.onSelectedCategory(it) }
        }
    }
}