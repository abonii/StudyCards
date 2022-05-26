package abm.co.studycards.ui.vocabulary

import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentVocabularyTabBinding
import abm.co.studycards.domain.model.Word
import abm.co.studycards.domain.model.examplesToString
import abm.co.studycards.ui.show_example_dialog.ShowExampleDialogFragment
import abm.co.studycards.util.Constants.VOCABULARY_TAB_POSITION
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initWords(arguments?.getInt(VOCABULARY_TAB_POSITION) ?: 0)
    }


    override fun initUI(savedInstanceState: Bundle?) {
        adapterV = VocabularyAdapter(
            viewModel::changeType,
            viewModel.currentTabType
        ).apply {
            onLongClickWord = ::onLongClickWord
        }
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.stateFlow.collectLatest {
                when (it) {
                    is VocabularyUiState.Error -> {
                        errorOccurred(it.error)
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

    private fun errorOccurred(@StringRes errorMsg: Int) = binding.run {
        text.visibility = View.VISIBLE
        text.text = getText(errorMsg)
        recyclerView.visibility = View.GONE
        shimmerLayout.hideShimmer()
        shimmerLayout.visibility = View.GONE
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.adapter = adapterV
    }

    private fun onSuccess(value: List<Word>) = binding.run {
        recyclerView.visibility = View.VISIBLE
        text.visibility = View.GONE
        shimmerLayout.hideShimmer()
        shimmerLayout.visibility = View.GONE
        adapterV?.submitList(value)
    }

    private fun onLoading() = binding.run {
        shimmerLayout.startShimmer()
        shimmerLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        text.visibility = View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        adapterV?.saveStates(outState)
    }

    private fun onLongClickWord(word: Word) {
        val examples = word.examplesToString()
        if (examples.isBlank()) {
            toast(R.string.no_example)
        } else {
            val exampleDialogFragment =
                ShowExampleDialogFragment.newInstance(examples = examples, name = word.name)
            exampleDialogFragment.show(childFragmentManager, ShowExampleDialogFragment.NAME)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        try {
            super.onViewStateRestored(savedInstanceState)
            adapterV?.restoreStates(savedInstanceState)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapterV = null
    }

}
