package abm.co.studycards.ui.add_word.dialog.category

import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentSelectCategoryDialogBinding
import abm.co.studycards.util.Constants.REQUEST_CATEGORY_KEY
import abm.co.studycards.util.base.BaseDialogFragment
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import abm.co.studycards.util.navigate
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SelectCategoryDialogFragment :
    BaseDialogFragment<FragmentSelectCategoryDialogBinding>(R.layout.fragment_select_category_dialog) {

    private val viewModel: SelectCategoryDialogViewModel by viewModels()
    private lateinit var selectAdapter: SelectCategoryAdapter

    override fun initUI(savedInstanceState: Bundle?) {
        collectData()
        initRV()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.run {
            createCategory.setOnClickListener { directToCreateCategory() }
            clear.setOnClickListener { dismiss() }
            save.setOnClickListener { onDone() }
        }
    }

    fun collectData() {
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.allCategoryStateFlow.collectLatest {
                selectAdapter.submitList(it)
            }
        }
    }

    private fun initRV() {
        selectAdapter = SelectCategoryAdapter()
        binding.recyclerView.adapter = selectAdapter
    }

    private fun onDone() {
        val result = selectAdapter.currentList[selectAdapter.selectedItemPos].category
        setFragmentResult(REQUEST_CATEGORY_KEY, bundleOf("category" to result))
        dismiss()
    }

    private fun directToCreateCategory() {
        val action = SelectCategoryDialogFragmentDirections
            .actionSelectCategoryDialogFragmentToAddEditCategoryFragment()
        navigate(action)
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.7).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.7).toInt()
        dialog!!.window?.setLayout(width, height)
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner)
    }

}