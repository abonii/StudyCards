package abm.co.studycards.ui.add_word.dialog.translated

import abm.co.studycards.R
import abm.co.studycards.data.model.oxford.ResultsEntry
import abm.co.studycards.databinding.FragmentTranslatedWordDialogBinding
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import abm.co.studycards.ui.add_word.dialog.translated.adapters.TranslatedCategoryAdapter
import abm.co.studycards.ui.add_word.dialog.translated.adapters.TranslatedWordAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TranslatedWordDialogFragment(
    private val entry: ResultsEntry?,
    private val listener: OnFinishDialog,
    private val fromTarget: Boolean
) : DialogFragment(), TranslatedWordAdapter.OnCheckBoxClicked {

    private var _binding: FragmentTranslatedWordDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TranslatedWordDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        _binding = FragmentTranslatedWordDialogBinding.inflate(inflater, container, false)
        viewModel.loadWord(entry)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tAdapter = TranslatedCategoryAdapter(this)
        tAdapter.items = viewModel.entries?.lexicalEntries
        binding.apply {
            word.text = viewModel.wordName.uppercase()
            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = tAdapter
            }
            save.setOnClickListener {
                listener.onFinish(
                    viewModel.selectedExamples.toList(),
                    viewModel.selectedTranslations,
                    fromTarget
                )
                dismiss()
            }
            cancel.setOnClickListener {
                dismiss()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                nested.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                    if (scrollY > oldScrollY && viewModel.clickable && scrollY - oldScrollY > 10) {
                        slideDown()
                    } else if (scrollY < oldScrollY && !viewModel.clickable && oldScrollY - scrollY > 10) {
                        slideUp()
                    }
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
    }

    override fun onExampleSelected(example: String, isPressed: Boolean) {
        viewModel.onExampleSelected(example, isPressed)
    }

    override fun onTranslationSelected(translation: String, isPressed: Boolean) {
        viewModel.onTranslationSelected(translation, isPressed)
    }

    interface OnFinishDialog {
        fun onFinish(examples: List<String>, translations: List<String>, fromTarget: Boolean)
    }
}