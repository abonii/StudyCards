package abm.co.studycards.ui.add_category

import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentAddEditCategoryBinding
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
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
        _binding = FragmentAddEditCategoryBinding.bind(view)
        binding.apply {
            category.apply {
                setText(viewModel.mainName)
                addTextChangedListener {
                    viewModel.mainName = it.toString()
                }
            }
            save.setOnClickListener {
                viewModel.saveCategory()
                dismiss()
            }
            cancel.setOnClickListener { dismiss() }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.run {
            setLayout(
                (Resources.getSystem().displayMetrics.widthPixels * 0.9).toInt(),
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawableResource(R.drawable.round_corner)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}