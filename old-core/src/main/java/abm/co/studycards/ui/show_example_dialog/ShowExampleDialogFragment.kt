package abm.co.studycards.ui.show_example_dialog

import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentShowExampleDialogBinding
import abm.co.studycards.util.base.BaseDialogFragment
import android.content.res.Resources
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowExampleDialogFragment :
    BaseDialogFragment<FragmentShowExampleDialogBinding>(R.layout.fragment_show_example_dialog) {

    private var examples: String? = null
    private var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            examples = it.getString(EXAMPLES_PARAM)
            name = it.getString(NAME_PARAM)
        }
    }


    override fun initUI(savedInstanceState: Bundle?) = binding.run {
        examplesTv.text = examples
        nameTv.text = name
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.run {
            setLayout(
                (Resources.getSystem().displayMetrics.widthPixels * 0.8).toInt(),
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawableResource(R.drawable.round_corner)
        }
    }

    companion object {

        private const val EXAMPLES_PARAM = "EXAMPLE_PARAM"
        private const val NAME_PARAM = "NAME_PARAM"
        const val NAME = "SHOW_EXAMPLES"

        @JvmStatic
        fun newInstance(name:String, examples: String) =
            ShowExampleDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(EXAMPLES_PARAM, examples)
                    putString(NAME_PARAM, name)
                }
            }
    }
}