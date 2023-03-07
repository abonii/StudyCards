package abm.co.studycards.ui.in_category

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.databinding.FragmentInCategoryBinding
import abm.co.studycards.domain.model.Word
import abm.co.studycards.util.helpers.SwipeToDeleteCallback
import abm.co.studycards.setDefaultStatusBar
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import abm.co.studycards.util.navigate
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class InCategoryFragment :
    BaseBindingFragment<FragmentInCategoryBinding>(R.layout.fragment_in_category) {

    private var wordsAdapter: WordsAdapter? = null
    private val viewModel: InCategoryViewModel by viewModels()
    private var snackbar: Snackbar? = null
    private var textToSpeech: TextToSpeech? = null

    override fun initUI(savedInstanceState: Bundle?) {
        binding.inCategoryFragment = this
        setUpRecyclerView()
        collectData()
        setToolbar()
        initTextToSpeech()
    }

    private fun setToolbar() {
        requireActivity().setDefaultStatusBar()
        (activity as MainActivity).setToolbar(binding.toolbar)
    }


    private fun collectData() {
        launchAndRepeatWithViewLifecycle(Lifecycle.State.CREATED) {
            viewModel.stateFlow.collect {
                when (it) {
                    is InCategoryUiState.Error -> errorOccurred(it.msg)
                    is InCategoryUiState.Success -> onSuccess(it.category)
                }
            }
        }
        launchAndRepeatWithViewLifecycle(Lifecycle.State.CREATED) {
            viewModel.categoryStateFlow.collectLatest {
                binding.folderName.text = it.name
            }
        }
    }

    private fun onSuccess(value: List<Word>) = binding.run {
        wordsAdapter = WordsAdapter(::onItemClick, ::onAudioClicked)
        wordsAdapter?.submitList(value)
        binding.recyclerView.adapter = wordsAdapter
        recyclerView.visibility = View.VISIBLE
        error.visibility = View.GONE
    }


    private fun errorOccurred(@StringRes text: Int) = binding.run {
        error.visibility = View.VISIBLE
        error.text = getString(text)
        recyclerView.visibility = View.GONE
    }

    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(requireContext()) {
            if (it != TextToSpeech.ERROR) {
                isLanguageAvailable(Locale(viewModel.targetLang))
            }
        }
    }

    private fun isLanguageAvailable(language: Locale) {
        lifecycleScope.launch(Dispatchers.Default) {
            var available = false
            when (textToSpeech?.isLanguageAvailable(language)) {
                TextToSpeech.LANG_AVAILABLE, TextToSpeech.LANG_COUNTRY_AVAILABLE,
                TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE -> {
                    textToSpeech?.language = language
                    val voice = textToSpeech?.voice
                    val features = voice?.features
                    if (features != null && !features.contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED))
                        available = true
                }
            }
            viewModel.isLanguageInstalled = available
        }
    }

    private fun setUpRecyclerView() {
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback())
        binding.recyclerView.run {
            addItemDecoration(getItemDecoration())
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    private fun getItemDecoration() =
        DividerItemDecoration(
            context,
            DividerItemDecoration.VERTICAL
        )

    private fun showUndoSnackBar(deletedWord: Word) {
        val text = deletedWord.name
        snackbar = Snackbar.make(binding.recyclerView, text, Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.undo)) {
                viewModel.insertWord(deletedWord)
            }
        snackbar?.show()
    }

    fun directToEditCategory() {
        val action =
            InCategoryFragmentDirections
                .actionInCategoryFragmentToAddEditCategoryFragment(
                    viewModel.categoryStateFlow.value.copy(
                        words = emptyList()
                    )
                )
        navigate(action)
    }


    private fun openDownloadTTSDialog() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val pm: PackageManager = requireActivity().packageManager
                val installIntent = Intent()
                installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                val resolveInfo =
                    pm.resolveActivity(installIntent, PackageManager.MATCH_DEFAULT_ONLY)
                if (resolveInfo == null) {
                    toast(getString(R.string.this_function_not_available))
                } else {
                    startActivity(installIntent)
                }
            }
        } catch (e: ActivityNotFoundException) {
            toast(getString(R.string.this_function_not_available))
        }
    }

    fun onFloatingActionWordClick() {
        val action =
            InCategoryFragmentDirections.actionInCategoryFragmentToAddEditWordFragment(
                categoryName = viewModel.categoryStateFlow.value.name,
                categoryId = viewModel.category.id
            )
        navigate(action)
    }

    private fun onItemClick(word: Word) {
        val currentState = viewModel.stateFlow.value
        if (currentState is InCategoryUiState.Success) {
            val action =
                InCategoryFragmentDirections.actionInCategoryFragmentToAddEditWordFragment(
                    word = word,
                    categoryName = viewModel.categoryStateFlow.value.name,
                    categoryId = viewModel.category.id
                )
            navigate(action)
        }
    }

    private fun onAudioClicked(word: Word) {
        if (!viewModel.isLanguageInstalled) {
            toast(getString(R.string.you_do_n_have_language_to_speech))
            openDownloadTTSDialog()
        } else {
            val newText = word.translations
            textToSpeech?.speak(
                newText,
                TextToSpeech.QUEUE_FLUSH,
                null,
                newText
            )
        }
    }

    private fun itemTouchHelperCallback() = object : SwipeToDeleteCallback(requireContext()) {
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.absoluteAdapterPosition
            val deletedWord = wordsAdapter?.getItem(position)
            if (deletedWord != null) {
                showUndoSnackBar(deletedWord)
                viewModel.deleteWord(deletedWord)
            }
        }
    }

    override fun onDestroyView() {
        snackbar?.dismiss()
        textToSpeech?.shutdown()
        wordsAdapter = null
        super.onDestroyView()
    }
}