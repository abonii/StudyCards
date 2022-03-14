package abm.co.studycards.ui.vocabulary

import abm.co.studycards.R
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.databinding.FragmentVocabularyTabBinding
import abm.co.studycards.util.Constants.VOCABULARY_TAB_POSITION
import abm.co.studycards.util.base.BaseBindingFragment
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class VocabularyTabFragment :
    BaseBindingFragment<FragmentVocabularyTabBinding>(R.layout.fragment_vocabulary_tab) {

    companion object {
        @JvmStatic
        fun newInstance(fragmentPosition: Int) = VocabularyTabFragment().apply {
            arguments = bundleOf(VOCABULARY_TAB_POSITION to fragmentPosition)
        }
    }

    private var adapterV: VocabularyAdapter? = null
    private val viewModel: VocabularyViewModel by viewModels()


    override fun initUI(savedInstanceState: Bundle?) {
        viewModel.initWords(arguments?.getInt(VOCABULARY_TAB_POSITION) ?: 0)
        adapterV = VocabularyAdapter(
            viewModel::changeType,
            viewModel.currentTabType,
            viewModel.firstBtnType,
            viewModel.secondBtnType
        )
        lifecycleScope.launch {
            viewModel.stateFlow.collectLatest {
                when (it) {
                    is VocabularyUiState.Error -> {
                        binding.text.text = getString(R.string.error_occurred)
                    }
                    VocabularyUiState.Loading -> {
                        onLoading()
                    }
                    is VocabularyUiState.Success -> {
                        onSuccess(it.value)
                    }
                }
            }
        }
        setUpRecyclerView()
    }


    private fun setUpRecyclerView() {
        binding.recyclerView.adapter = adapterV
    }

    private fun onSuccess(value: List<Word>) = binding.run{
        recyclerView.visibility = View.VISIBLE
        shimmerLayout.stopShimmer()
        shimmerLayout.hideShimmer()
        shimmerLayout.visibility = View.GONE
        adapterV?.submitList(value)
    }
    private fun onLoading() = binding.run{
        shimmerLayout.startShimmer()
        shimmerLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        adapterV?.saveStates(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        adapterV?.restoreStates(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapterV = null
    }

}
