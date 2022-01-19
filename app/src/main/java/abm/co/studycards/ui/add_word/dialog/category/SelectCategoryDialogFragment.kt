package abm.co.studycards.ui.add_word.dialog.category

import abm.co.studycards.R
import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.databinding.FragmentSelectCategoryDialogBinding
import abm.co.studycards.util.navigate
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.firebase.ui.database.FirebaseRecyclerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectCategoryDialogFragment : DialogFragment(R.layout.fragment_select_category_dialog),
    SelectCategoryAdapter.SelectCategoryAdapterListener {

    private lateinit var options: FirebaseRecyclerOptions<Category>
    private val viewModel: SelectCategoryDialogViewModel by viewModels()
    private var _binding: FragmentSelectCategoryDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var selectAdapter: SelectCategoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSelectCategoryDialogBinding.bind(view)
        initUi()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        viewModel.categoryLive.observe(viewLifecycleOwner) {
            selectAdapter.checked = it
        }
        binding.apply {
            createCategory.setOnClickListener {
                directToCreateCategory()
            }
            clear.setOnClickListener { dismiss() }
            save.setOnClickListener { onDone() }
        }
    }

    private fun initUi() {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        initRV()
    }

    private fun initRV() {
        options = FirebaseRecyclerOptions.Builder<Category>()
            .setQuery(viewModel.categoriesDbRef, Category::class.java)
            .build()
        selectAdapter = SelectCategoryAdapter(this, options)
        binding.apply {
            recyclerView.adapter = selectAdapter
            recyclerView.itemAnimator = null
        }
    }

    private fun onDone() {
        val result = viewModel.categoryLive.value
        setFragmentResult("requestCategory", bundleOf("category" to result))
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
        selectAdapter.startListening()
    }

    override fun onRadioClicked(
        currentItem: Category
    ) {
        viewModel.categoryLive.postValue(currentItem)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}