package abm.co.studycards.ui.add_word.dialog.dictionary

import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentDictionaryDialogBinding
import abm.co.studycards.ui.add_word.dialog.dictionary.adapters.TranslatedCategoryAdapter
import abm.co.studycards.util.Constants
import abm.co.studycards.util.Constants.EXAMPLES_SEPARATOR
import abm.co.studycards.util.Constants.TRANSLATIONS_SEPARATOR
import abm.co.studycards.util.base.BaseDialogFragment
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DictionaryDialogFragment :
    BaseDialogFragment<FragmentDictionaryDialogBinding>(R.layout.fragment_dictionary_dialog) {

    private val viewModel: DictionaryViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        val tAdapter = TranslatedCategoryAdapter(::onExampleSelected, ::onTranslationSelected)
        tAdapter.submitList(viewModel.entries?.lexicalCategories)
        binding.run {
            word.text = viewModel.wordName.uppercase()
            recyclerView.adapter = tAdapter
            save.setOnClickListener {
                onDone()
            }
            cancel.setOnClickListener {
                dismiss()
            }
            nested.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                if (scrollY > oldScrollY && viewModel.clickable && scrollY - oldScrollY > 10) {
                    slideDown()
                } else if (scrollY < oldScrollY && !viewModel.clickable && oldScrollY - scrollY > 10) {
                    slideUp()
                }
            }
        }
    }

    private fun slideDown() {
        viewModel.clickable = false
        binding.layoutButtons.animate().translationY(200F)
    }

    private fun slideUp() {
        viewModel.clickable = true
        binding.layoutButtons.animate().translationY(0F)
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner)
    }

    private fun onExampleSelected(example: String, isPressed: Boolean) {
        viewModel.onExampleSelected(example, isPressed)
    }

    private fun onTranslationSelected(translation: String, isPressed: Boolean) {
        viewModel.onTranslationSelected(translation, isPressed)
    }

    private fun onDone() {
        setFragmentResult(
            Constants.REQUEST_DICTIONARY_KEY, bundleOf(
                "from_target" to viewModel.fromTarget,
                "selected_examples" to (viewModel.selectedExamples.joinToString(separator = EXAMPLES_SEPARATOR)),
                "selected_translations" to viewModel.selectedTranslations.joinToString(separator = TRANSLATIONS_SEPARATOR)
            )
        )
        dismiss()
    }
}