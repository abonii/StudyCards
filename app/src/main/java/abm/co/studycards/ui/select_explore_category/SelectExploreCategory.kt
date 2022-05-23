package abm.co.studycards.ui.select_explore_category

import abm.co.studycards.R
import abm.co.studycards.domain.model.Category
import abm.co.studycards.databinding.FragmentSelectExploreSetBinding
import abm.co.studycards.ui.home.CategoryUiState
import abm.co.studycards.util.base.BaseDialogFragment
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import android.content.res.Resources
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SelectExploreCategory :
    BaseDialogFragment<FragmentSelectExploreSetBinding>(R.layout.fragment_select_explore_set) {

    private val viewModel: SelectExploreCategoryViewModel by viewModels()
    private lateinit var exploreSetAdapter: SelectExploreCategoryAdapter

    private lateinit var setId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setId = arguments?.getString(SET_ID_PARAM, "").toString()
        viewModel.fetchTheSetCategory(setId)
    }

    override fun initUI(savedInstanceState: Bundle?) {
        collectViewModel()
        with(binding) {
            exploreSetAdapter = SelectExploreCategoryAdapter { category ->
                onClickSelectExploreSet(category)
            }
            setsRv.adapter = exploreSetAdapter
        }
    }

    private fun collectViewModel() {
        launchAndRepeatWithViewLifecycle {
            viewModel.toast.collectLatest {
                toast(it)
            }
        }
        launchAndRepeatWithViewLifecycle(Lifecycle.State.CREATED) {
            viewModel.stateFlow.collectLatest {
                when (it) {
                    is CategoryUiState.Error -> {
                        binding.error.text = getString(it.msg)
                        binding.error.isVisible = true
                        binding.progressBar.isVisible = false
                        binding.setsRv.isVisible = false
                    }
                    CategoryUiState.Loading -> {
                        binding.error.isVisible = false
                        binding.progressBar.isVisible = true
                        binding.setsRv.isVisible = false
                    }
                    is CategoryUiState.Success -> {
                        binding.error.isVisible = false
                        binding.progressBar.isVisible = false
                        binding.setsRv.isVisible = true
                        exploreSetAdapter.submitList(it.value)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.run {
            setLayout(
                (Resources.getSystem().displayMetrics.widthPixels * 0.9).toInt(),
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawableResource(R.drawable.round_corner)
        }
    }

    private fun onClickSelectExploreSet(category: Category) {
        viewModel.addUserCategoryToExplore(setId, category)
    }

    companion object {

        private const val SET_ID_PARAM = "SET_ID_PARAM"
        const val NAME = "SELECT_EXPLORE_SET"

        @JvmStatic
        fun newInstance(setId: String) =
            SelectExploreCategory().apply {
                arguments = Bundle().apply {
                    putString(SET_ID_PARAM, setId)
                }
            }
    }

}