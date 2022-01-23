package abm.co.studycards.ui.add_word

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.data.model.AvailableLanguages
import abm.co.studycards.data.model.oxford.ResultsEntry
import abm.co.studycards.databinding.FragmentAddEditWordBinding
import abm.co.studycards.ui.add_word.dialog.translated.TranslatedWordDialogFragment
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.getProgressBarDrawable
import abm.co.studycards.util.navigate
import android.content.Context
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class AddEditWordFragment :
    BaseBindingFragment<FragmentAddEditWordBinding>(R.layout.fragment_add_edit_word),
    TranslatedWordDialogFragment.OnFinishDialog {

    private val viewModel: AddEditWordViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        setToolbar()
        initBindings()
        setImage()
        collectData()
        setResultListener()
    }

    private fun setToolbar() {
        (activity as MainActivity).setToolbar(binding.toolbar, findNavController())
    }

    private fun setResultListener() {
        setFragmentResultListener("requestCategory") { _, bundle ->
            viewModel.changeCategory(bundle.getParcelable("category"))
        }
        setFragmentResultListener("requestImage") { _, bundle ->
            viewModel.imageUrl = bundle.getString("imageUrl")
            setImage()
        }
    }

    private fun collectData() {
        lifecycleScope.launchWhenResumed {
            viewModel.stateFlow.collectLatest {
                when (it) {
                    AddEditWordUi.Default -> {}
                    is AddEditWordUi.CategoryChanged -> {
                        binding.category.text = it.category
                    }
                    is AddEditWordUi.Error -> {
                        binding.translatedWith.text =
                            it.errorStatus?.name ?: getString(R.string.some_problems_occured)
                    }
                    is AddEditWordUi.SuccessOxford -> {
                        directToOxfordWordsDialog(it.value, it.fromSource)
                        removeProgressBarIcon(it.fromSource)
//                        binding.translatedWith.text = getString(R.string.translated_with_oxford)
                    }
                    is AddEditWordUi.SuccessYandex -> {
                        yandexResponse(it.value, it.fromSource)
                        removeProgressBarIcon(it.fromSource)
//                        binding.translatedWith.text = getString(R.string.translated_with_yandex)
                    }
                    is AddEditWordUi.Loading -> {
                        if (it.fromSource) {
                            setProgressBar(binding.wordLayout)
                        } else setProgressBar(binding.translatedLayout)
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.toast.collectLatest {
                toast(it)
            }
        }
        viewModel.userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.child("canTranslateTimeEveryDay").value?.let {
                        viewModel.translateCounts = it as Long
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                showLog(error.message)
            }

        })
    }

    private fun yandexResponse(value: String, fromSource: Boolean) {
        if (fromSource) {
            binding.translated.setText(value)
        } else binding.word.setText(value)
    }

    private fun initBindings() {

        binding.apply {
            wordLayout.hint =
                AvailableLanguages.getLanguageNameByCode(requireContext(), viewModel.sourceLanguage)
            wordLayout.setEndIconOnClickListener {
                viewModel.fetchWord(true)
            }
            translatedLayout.hint =
                AvailableLanguages.getLanguageNameByCode(requireContext(), viewModel.targetLanguage)
            translatedLayout.setEndIconOnClickListener {
                viewModel.fetchWord(false)
            }
            save.setOnClickListener {
                if (viewModel.onSaveClick())
                    findNavController().popBackStack()
            }
            word.apply {
                addTextChangedListener {
                    viewModel.sourceWord = it.toString()
                    removeProgressBarIcon(true)
                }
                setText(viewModel.sourceWord)
            }
            translated.apply {
                addTextChangedListener {
                    viewModel.targetWord = it.toString()
                    removeProgressBarIcon(false)
                }
                setText(viewModel.targetWord)
            }
            example.apply {
                addTextChangedListener {
                    viewModel.exampleText = it.toString()
                }
                setText(viewModel.exampleText)
            }
            containerLayout.setOnClickListener {
                onClickOutside(it)
            }
            category.setOnClickListener {
                directToCategoryDialogFragment()
            }
            image.setOnClickListener {
                directToChooseImage()
            }
        }
    }

    private fun removeProgressBarIcon(fromSource: Boolean) {
        if (fromSource) {
            if (viewModel.isSourceWordTranslatedOxford())
                setNotesIcon(binding.wordLayout)
            else
                setTranslateIcon(binding.wordLayout)
        } else {
            if (viewModel.isTargetWordTranslatedOxford())
                setNotesIcon(binding.translatedLayout)
            else
                setTranslateIcon(binding.translatedLayout)
        }
    }

    private fun setImage() {
        viewModel.imageUrl?.let {
            Glide.with(requireContext())
                .load(viewModel.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_image_search)
                .into(binding.image)
        }
    }

    private fun directToChooseImage() {
        val action = AddEditWordFragmentDirections
            .actionAddEditWordFragmentToImageDialogFragment(viewModel.word?.name)
        navigate(action)
    }


    private fun setProgressBar(view: TextInputLayout) {
        view.endIconDrawable = context?.getProgressBarDrawable().apply {
            (this as Animatable).start()
        }
    }

    private fun setTranslateIcon(view: TextInputLayout) {
        view.endIconDrawable = getDrawable(requireContext(), R.drawable.ic_translate)
    }

    private fun setNotesIcon(view: TextInputLayout) {
        view.endIconDrawable = getDrawable(requireContext(), R.drawable.ic_notes)
    }

    private fun directToCategoryDialogFragment() {
        val action =
            AddEditWordFragmentDirections
                .actionAddEditWordFragmentToSelectCategoryDialogFragment(viewModel.currentCategoryId)
        navigate(action)
    }


    private fun directToOxfordWordsDialog(entry: ResultsEntry, fromSource: Boolean) {
        TranslatedWordDialogFragment(
            entry, this, !fromSource
        ).show(childFragmentManager, "TranslatedWordsDialogFragment")
    }

    override fun onFinish(
        examples: List<String>,
        translations: List<String>,
        fromTarget: Boolean,
    ) {
        binding.example.setText(examples.joinToString(separator = "\n"))
        if (fromTarget) {
            binding.word.setText(translations.joinToString(separator = ", "))
        } else {
            binding.translated.setText(translations.joinToString(separator = ", "))
        }
    }

    private fun onClickOutside(view: View) {
        val imm: InputMethodManager = view.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
//const val TAG = "AddEditFragment"
