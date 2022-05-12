package abm.co.studycards.ui.add_to_yourself

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.data.model.WordX
import abm.co.studycards.databinding.FragmentAddYourlsefBinding
import abm.co.studycards.setDefaultStatusBar
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddYourselfFragment :
    BaseBindingFragment<FragmentAddYourlsefBinding>(R.layout.fragment_add_yourlsef) {

    private var wordsAdapter: ExploreWordsForSelectingAdapter? = null
    private val viewModel: AddYourselfViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        binding.fragment = this
        binding.viewmodel = viewModel
        collectData()
        setToolbarAndStatusBar()
        setUpRecyclerView()
    }

    private fun setToolbarAndStatusBar() {
        requireActivity().setDefaultStatusBar()
        (activity as MainActivity).setToolbar(binding.toolbar)
        binding.toolbar.setNavigationIcon(R.drawable.ic_clear)
        binding.toolbarTitle.text = getString(R.string.select_words)
    }


    private fun collectData() {
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.stateFlow.collect {
                when (it) {
                    is AddYourselfUiState.Error -> errorOccurred(it.msg)
                    AddYourselfUiState.Loading -> onLoading()
                    is AddYourselfUiState.Success -> onSuccess(it.value)
                }
            }
        }
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.isAllSelected.collectLatest {
                binding.checkAll.isChecked = it
                wordsAdapter?.updateAllWords(it)
            }
        }
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.sharedFlow.collectLatest {
                when (it) {
                    AddYourselfEventChannel.NavigateBack -> {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun onSuccess(value: List<WordX>) = binding.run {
        wordsAdapter?.submitList(value)
        progressBar.visibility = View.GONE
        error.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        recyclerView.alpha = 1f
    }

    private fun onLoading() = binding.run {
        error.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        recyclerView.alpha = 0.5f
    }

    private fun errorOccurred(@StringRes text: Int) = binding.run {
        error.visibility = View.VISIBLE
        error.text = getString(text)
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
    }

    private fun setUpRecyclerView() {
        wordsAdapter = ExploreWordsForSelectingAdapter {
            binding.checkAll.isChecked = viewModel.isSelectedAllWords()

        }
        binding.recyclerView.run {
            adapter = wordsAdapter
            addItemDecoration(getItemDecoration())
        }
    }


    private fun getItemDecoration() =
        DividerItemDecoration(
            context,
            DividerItemDecoration.VERTICAL
        )

    override fun onDestroyView() {
        wordsAdapter = null
        super.onDestroyView()
    }
}