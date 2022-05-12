package abm.co.studycards.ui.add_category

import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentAddEditCategoryBinding
import abm.co.studycards.util.base.BaseDialogFragment
import android.content.res.Resources
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditCategoryFragment :
    BaseDialogFragment<FragmentAddEditCategoryBinding>(R.layout.fragment_add_edit_category) {

    private val viewModel: AddEditCategoryViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        binding.run {
            viewmodel = viewModel
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

}