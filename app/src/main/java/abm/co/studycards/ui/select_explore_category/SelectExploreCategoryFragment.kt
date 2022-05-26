package abm.co.studycards.ui.select_explore_category

import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentSelectExploreCategoryBinding
import abm.co.studycards.domain.model.Category
import abm.co.studycards.domain.model.ResultWrapper
import abm.co.studycards.util.base.BaseDialogFragment
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SelectExploreCategoryFragment :
    BaseDialogFragment<FragmentSelectExploreCategoryBinding>(R.layout.fragment_select_explore_category) {

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
                    is ResultWrapper.Error -> {
                        binding.error.text = getString(it.res)
                        binding.error.isVisible = true
                        binding.progressBar.isVisible = false
                        binding.setsRv.isVisible = false
                    }
                    ResultWrapper.Loading -> {
                        binding.error.isVisible = false
                        binding.progressBar.isVisible = true
                        binding.setsRv.isVisible = false
                    }
                    is ResultWrapper.Success -> {
                        exploreSetAdapter.submitList(it.value)
                        binding.error.isVisible = false
                        binding.progressBar.isVisible = false
                        binding.setsRv.isVisible = true
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.run {
            setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                (resources.displayMetrics.heightPixels * 0.35).toInt()
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
            SelectExploreCategoryFragment().apply {
                arguments = Bundle().apply {
                    putString(SET_ID_PARAM, setId)
                }
            }
    }

}