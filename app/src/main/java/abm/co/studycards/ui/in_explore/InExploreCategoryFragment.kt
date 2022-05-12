package abm.co.studycards.ui.in_explore

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.databinding.FragmentInExploreCategoryBinding
import abm.co.studycards.helpers.MarginItemDecoration
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import abm.co.studycards.util.navigate
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InExploreCategoryFragment :
    BaseBindingFragment<FragmentInExploreCategoryBinding>(R.layout.fragment_in_explore_category) {

    private var wordsAdapter: ExploreWordsAdapter? = null
    private val viewModel: InExploreViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        binding.inExploreFragment = this
        binding.viewmodel = viewModel
        setToolbarAndStatusBar()
        collectData()
        setUpRecyclerView()
    }

    private fun setToolbarAndStatusBar() {
        requireActivity().window.statusBarColor =
            resources.getColor(R.color.tab_background, null)
        (activity as MainActivity).setToolbar(binding.toolbar)
        binding.folderName.text = viewModel.category.mainName
    }


    private fun collectData() {
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.stateFlow.collect {
                when (it) {
                    is InExploreUiState.Error -> errorOccurred(it.msg)
                    InExploreUiState.Loading -> onLoading()
                    is InExploreUiState.Success -> onSuccess(it.value)
                }
            }
        }
    }

    private fun onSuccess(value: List<Word>) = binding.run {
        wordsAdapter?.submitList(value)
        progressBar.visibility = View.GONE
        error.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun onLoading() = binding.run {
        error.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun errorOccurred(@StringRes text: Int) = binding.run {
        error.visibility = View.VISIBLE
        error.text = getText(text)
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
    }

    private fun setUpRecyclerView() {
        wordsAdapter = ExploreWordsAdapter()
        binding.recyclerView.apply {
            adapter = wordsAdapter
            addItemDecoration(getItemDecoration())
        }
    }

    fun addToYourself() {
        val nav =
            InExploreCategoryFragmentDirections.actionInExploreCategoryFragmentToAddYourselfFragment(
                category = viewModel.category,
                setId = viewModel.setId
            )
        navigate(nav)
    }

    private fun getItemDecoration() =
        MarginItemDecoration(requireContext(), DividerItemDecoration.VERTICAL, 1)

    override fun onDestroyView() {
        wordsAdapter = null
        super.onDestroyView()
    }
}


