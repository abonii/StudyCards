package abm.co.studycards.ui.explore

import abm.co.studycards.R
import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.databinding.FragmentExploreBinding
import abm.co.studycards.setDefaultStatusBar
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import abm.co.studycards.util.navigate
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ExploreFragment : BaseBindingFragment<FragmentExploreBinding>(R.layout.fragment_explore) {

    private val viewModel: ExploreViewModel by viewModels()
    private var parentAdapter: ParentExploreAdapter? = null

    override fun initUI(savedInstanceState: Bundle?) {
        requireActivity().setDefaultStatusBar()
        parentAdapter = ParentExploreAdapter(::onClickItem)
        binding.parentRecyclerView.adapter = parentAdapter
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

    private fun onSuccess(value: List<ParentExploreUI>) = binding.run {
        parentRecyclerView.visibility = View.VISIBLE
        stopShimmer()
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
        parentRecyclerView.visibility = View.GONE
    }

    private fun errorOccurred(value: String) = binding.run {
        error.text = value
        error.visibility = View.VISIBLE
        parentRecyclerView.visibility = View.GONE
        stopShimmer()
    }

    private fun onClickItem(category: Category) {
        val nav = ExploreFragmentDirections.actionExploreFragmentToInExploreCategoryFragment(
            category
        )
        navigate(nav)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        parentAdapter = null
    }

}