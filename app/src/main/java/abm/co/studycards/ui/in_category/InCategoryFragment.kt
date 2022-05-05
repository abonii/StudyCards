package abm.co.studycards.ui.in_category

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.model.vocabulary.translationsToString
import abm.co.studycards.databinding.FragmentInCategoryBinding
import abm.co.studycards.helpers.SwipeToDeleteCallback
import abm.co.studycards.setDefaultStatusBar
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.fromHtml
import abm.co.studycards.util.launchAndRepeatWithViewLifecycle
import abm.co.studycards.util.navigate
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class InCategoryFragment :
    BaseBindingFragment<FragmentInCategoryBinding>(R.layout.fragment_in_category) {

    private var wordsAdapter: WordsAdapter? = null
    private val viewModel: InCategoryViewModel by viewModels()
    private var snackbar: Snackbar? = null
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        collectData()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initUI(savedInstanceState: Bundle?) {
        binding.inCategoryFragment = this
        initTextToSpeech()
        setToolbar()
        initFAB()
        setUpRecyclerView()
    }

    private fun setToolbar() {
        requireActivity().setDefaultStatusBar()
        (activity as MainActivity).setToolbar(binding.toolbar, findNavController())
    }


    private fun collectData() {
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.stateFlow.collect {
                when (it) {
                    is InCategoryUiState.Error -> errorOccurred(it.msg)
                    InCategoryUiState.Loading -> onLoading()
                    is InCategoryUiState.Success -> onSuccess(it.value)
                }
            }
        }
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.categoryStateFlow.collect {
                if (it.mainName.isNotEmpty()) {
                    binding.folderName.text = it.mainName.uppercase()
                    viewModel.category = it
                }
            }
        }
    }

    private fun onSuccess(value: List<Word>) = binding.run {
        wordsAdapter?.submitList(value)
        progressBar.visibility = View.GONE
        error.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun onLoading() = binding.run {
        error.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun errorOccurred(text: String) = binding.run {
        error.visibility = View.VISIBLE
        error.text = text
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
    }

    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(requireContext()) {
            if (it != TextToSpeech.ERROR) {
                viewModel.isLanguageInstalled = isLanguageAvailable(Locale(viewModel.targetLang))
            }
        }
    }

    private fun isLanguageAvailable(language: Locale): Boolean {
        var available = false
        when (textToSpeech.isLanguageAvailable(language)) {
            TextToSpeech.LANG_AVAILABLE, TextToSpeech.LANG_COUNTRY_AVAILABLE, TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE -> {
                textToSpeech.language = language
                val voice = textToSpeech.voice
                val features = voice.features
                if (features != null && !features.contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED))
                    available = true
            }
        }
        return available
    }

    private fun setUpRecyclerView() {
        wordsAdapter = WordsAdapter(::onItemClick, ::onAudioClicked)
        binding.recyclerView.run {
            adapter = wordsAdapter
            addItemDecoration(getItemDecoration())
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback())
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun getItemDecoration() =
        DividerItemDecoration(
            context,
            DividerItemDecoration.VERTICAL
        )

    private fun showUndoSnackBar(deletedWord: Word) {
        val text = deletedWord.name.fromHtml()
        snackbar = Snackbar.make(binding.recyclerView, text, Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.undo)) {
                viewModel.insertWord(deletedWord)
            }
        snackbar?.show()
    }

    private fun initFAB() {
        binding.floatingActionButton.animate()
            .rotation(180f).duration = 500
    }

    fun directToEditCategory() {
        val action =
            InCategoryFragmentDirections.actionInCategoryFragmentToAddEditCategoryFragment(viewModel.category)
        navigate(action)
    }

    private fun openDownloadTTSDialog(){
        val installIntent = Intent()
        installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
        startActivity(installIntent)
    }

    fun onFloatingActionWordClick() {
        val action =
            InCategoryFragmentDirections.actionInCategoryFragmentToAddEditWordFragment(
                categoryName = viewModel.category.mainName,
                categoryId = viewModel.category.id
            )
        navigate(action)
    }


    private fun onItemClick(vocabulary: Word) {
        val action =
            InCategoryFragmentDirections.actionInCategoryFragmentToAddEditWordFragment(
                word = vocabulary,
                categoryName = viewModel.category.mainName,
                categoryId = viewModel.category.id
            )
        navigate(action)
    }

    private fun onAudioClicked(word: Word) {
        if (!viewModel.isLanguageInstalled) {
            toast(getString(R.string.you_do_n_have_language_to_speech))
            openDownloadTTSDialog()
        } else {
            val newText = word.translationsToString()
            textToSpeech.speak(
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
        textToSpeech.shutdown()
        wordsAdapter = null
        super.onDestroyView()
    }
}