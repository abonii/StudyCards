package abm.co.studycards.ui.explore

import abm.co.studycards.R
import abm.co.studycards.domain.model.Category
import abm.co.studycards.databinding.FragmentExploreBinding
import abm.co.studycards.setDefaultStatusBar
import abm.co.studycards.ui.select_explore_category.SelectExploreCategory
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import abm.co.studycards.util.navigate
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ExploreFragment : BaseBindingFragment<FragmentExploreBinding>(R.layout.fragment_explore) {

    private val viewModel: ExploreViewModel by viewModels()
    private var parentAdapter: ParentExploreAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        collectData()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun collectData() {
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.stateFlow.collectLatest {
                when (it) {
                    is ParentExploreUiState.Error -> {
                        errorOccurred(it.error)
                    }
                    ParentExploreUiState.Loading -> {
                        onLoading()
                    }
                    is ParentExploreUiState.Success -> {
                        onSuccess(it.value)
                    }
                }
            }
        }
    }

    override fun initUI(savedInstanceState: Bundle?) {
        requireActivity().setDefaultStatusBar()
        parentAdapter = ParentExploreAdapter(::onClickItem).apply {
            onClickAddSet = ::onClickAddSet
        }
        binding.parentRecyclerView.adapter = parentAdapter
    }

    private fun onSuccess(value: List<ParentExploreUI>) = binding.run {
        parentRecyclerView.visibility = View.VISIBLE
        stopShimmer()
        error.visibility = View.GONE
        parentAdapter?.submitList(value)
    }

    private fun stopShimmer() = binding.shimmerLayout.run {
        stopShimmer()
        hideShimmer()
        visibility = View.GONE
    }

    private fun startShimmer() = binding.shimmerLayout.run {
        startShimmer()
        visibility = View.VISIBLE
    }

    private fun onLoading() = binding.run {
        startShimmer()
        error.visibility = View.GONE
        parentRecyclerView.visibility = View.GONE
    }

    private fun errorOccurred(@StringRes value: Int) = binding.run {
        error.text = getString(value)
        error.visibility = View.VISIBLE
        parentRecyclerView.visibility = View.GONE
        stopShimmer()
    }

    private fun onClickItem(setId: String, category: Category) {
        val nav = ExploreFragmentDirections.actionExploreFragmentToInExploreCategoryFragment(
            category = category,
            setId = setId
        )
        navigate(nav)
    }

    private fun onClickAddSet(item: ParentExploreUI) {
        when (item) {
            is ParentExploreUI.SetUI -> {
                val selectExploreSet = SelectExploreCategory.newInstance(item.setId)
                selectExploreSet.show(childFragmentManager, SelectExploreCategory.NAME)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        parentAdapter = null
    }

}