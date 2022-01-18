package abm.co.studycards.ui.add_category

import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentAddEditCategoryBinding
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditCategoryFragment : DialogFragment(R.layout.fragment_add_edit_category) {

    private val viewModel: AddEditCategoryViewModel by viewModels()
    private var _binding: FragmentAddEditCategoryBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        _binding = FragmentAddEditCategoryBinding.bind(view)
        binding.apply {
            mainCategory.apply {
                setText(viewModel.mainName)
                addTextChangedListener {
                    viewModel.mainName = it.toString()
                }
            }
            save.setOnClickListener {
                viewModel.onSaveCategory()
                dismiss()
            }
            cancel.setOnClickListener { dismiss() }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}