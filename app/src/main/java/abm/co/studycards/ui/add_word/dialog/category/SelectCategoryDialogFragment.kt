package abm.co.studycards.ui.add_word.dialog.category

import abm.co.studycards.R
import abm.co.studycards.data.model.vocabulary.Category
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
import com.firebase.ui.database.FirebaseRecyclerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SelectCategoryDialogFragment :
    BaseDialogFragment<FragmentSelectCategoryDialogBinding>(R.layout.fragment_select_category_dialog),
    SelectCategoryAdapter.SelectCategoryAdapterListener {

    private lateinit var options: FirebaseRecyclerOptions<Category>
    private val viewModel: SelectCategoryDialogViewModel by viewModels()
    private lateinit var selectAdapter: SelectCategoryAdapter

    override fun initUI(savedInstanceState: Bundle?) {
        initRV()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.categoryStateFlow.collect {
                if (it != null) {
                    selectAdapter.checkedId = it.id
                }
            }
        }

        binding.run {
            createCategory.setOnClickListener {
                directToCreateCategory()
            }
            clear.setOnClickListener { dismiss() }
            save.setOnClickListener { onDone() }
        }
    }

    private fun initRV() {
        options = FirebaseRecyclerOptions.Builder<Category>()
            .setQuery(viewModel.categoriesDbRef, Category::class.java)
            .build()
        selectAdapter = SelectCategoryAdapter(this, options).apply {
            checkedId = viewModel.categoryId
        }
        binding.apply {
            recyclerView.adapter = selectAdapter
            recyclerView.itemAnimator = null
        }
    }

    private fun onDone() {
        val result = viewModel.categoryStateFlow.value
        setFragmentResult(REQUEST_CATEGORY_KEY, bundleOf("category" to result))
        dismiss()
    }

    override fun onStop() {
        super.onStop()
        selectAdapter.stopListening()
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
        selectAdapter.startListening()
    }

    override fun onRadioClicked(currentItem: Category) {
        viewModel.categoryStateFlow.value = currentItem
    }
}