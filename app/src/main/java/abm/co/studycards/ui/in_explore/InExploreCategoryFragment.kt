package abm.co.studycards.ui.in_explore

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentInExploreCategoryBinding
import abm.co.studycards.domain.model.Word
import abm.co.studycards.helpers.MarginItemDecoration
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import abm.co.studycards.util.navigate
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InExploreCategoryFragment :
    BaseBindingFragment<FragmentInExploreCategoryBinding>(R.layout.fragment_in_explore_category) {

    private var wordsAdapter: ExploreWordsAdapter? = null
    private val viewModel: InExploreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.iCreatedThisCategory) {
            setHasOptionsMenu(true)
        }
    }

    override fun initUI(savedInstanceState: Bundle?) {
        binding.inExploreFragment = this
        binding.viewmodel = viewModel
        collectData()
        setToolbarAndStatusBar()
        setUpRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (viewModel.iCreatedThisCategory) {
            inflater.inflate(R.menu.in_explore_category_menu, menu)
            super.onCreateOptionsMenu(menu, inflater)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_category -> {
                toast(R.string.will_be_available_soon)
                true
            }
            R.id.delete_category -> {
                lifecycleScope.launch(Dispatchers.Main) {
                    viewModel.deleteTheExploreCategory()
                    findNavController().popBackStack()
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun setToolbarAndStatusBar() {
        requireActivity().window.statusBarColor =
            resources.getColor(R.color.tab_background, null)
        (activity as MainActivity).setToolbar(binding.toolbar)
        binding.folderName.text = viewModel.category.name
    }


    private fun collectData() {
        launchAndRepeatWithViewLifecycle(Lifecycle.State.CREATED) {
            viewModel.stateFlow.collectLatest {
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
        recyclerView.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        error.visibility = View.GONE
        recyclerView.alpha = 1f
    }

    private fun onLoading() = binding.run {
        error.visibility = View.GONE
        recyclerView.alpha = 0.5f
        progressBar.visibility = View.VISIBLE
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
                categoryId = viewModel.category.id,
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


