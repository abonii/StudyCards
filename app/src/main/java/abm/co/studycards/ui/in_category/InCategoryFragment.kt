package abm.co.studycards.ui.in_category

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.databinding.FragmentInCategoryBinding
import abm.co.studycards.helpers.SwipeToDeleteCallback
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.navigate
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class InCategoryFragment :
    BaseBindingFragment<FragmentInCategoryBinding>(R.layout.fragment_in_category),
    WordsAdapter.OnItemClick {

    private val viewModel: InCategoryViewModel by viewModels()
    private var wordsAdapter: WordsAdapter = WordsAdapter(this, isLanguageInstalled())
    private var snackbar: Snackbar? = null
    private lateinit var textToSpeech: TextToSpeech

    override fun initViews(savedInstanceState: Bundle?) {
        initViews()
        setObservers()
//        setOnBackPressed()
    }

//    private fun setOnBackPressed() {
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    if (!wordsAdapter.isShortClickActivated) {
//                        isEnabled = false
//                        requireActivity().onBackPressed()
//                    } else {
//                        wordsAdapter.disableSelectableItems()
//                        onSelectItems(false, 0)
//                    }
//                }
//            })
//    }

    private fun setToolbar() {
        (activity as MainActivity).setToolbar(binding.toolbar, findNavController())
        binding.folderName.text = viewModel.category.mainName
    }

    private fun initViews() {
        binding.apply {
            floatingActionButton.setOnClickListener { onFloatingActionWordClick() }
            edit.setOnClickListener { directToEditCategory() }
//            deleteCategory.setOnClickListener { onDeleteClicked() }
        }
        setTextToSpeech()
        setToolbar()
        setUpRecyclerView()
        slideUpFAB()
    }

    private fun setTextToSpeech() {
        textToSpeech = TextToSpeech(requireContext()) {
            if (it != TextToSpeech.ERROR) {
                textToSpeech.language = Locale(viewModel.targetLang)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Occurred some problem with audio",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setObservers() {
        viewModel.categoryLiveData.observe(viewLifecycleOwner, {
            binding.folderName.text = it.mainName.uppercase()
            viewModel.category = it
        })
//        viewModel.getCategoryWithWords().observe(viewLifecycleOwner, {
//            if (it.isEmpty()) {
//                binding.recyclerView.visibility = View.GONE
//                binding.text.visibility = View.VISIBLE
//            }
//            if (!it.equals(wordsAdapter.items)) {
//                wordsAdapter.insertItems(it)
//            }
//        })
        viewModel.thisCategoryRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModel.categoryLiveData.postValue(snapshot.getValue(Category::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                showLog(error.message, "InCategoryCategoryRef")
            }
        })
        viewModel.wordsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<Word>()
                snapshot.children.forEach {
                    it.getValue(Word::class.java)?.let { it1 -> items.add(it1) }
                }
                wordsAdapter.submitList(items)
            }

            override fun onCancelled(error: DatabaseError) {
                showLog(error.message, "InCategoryWordRef")
            }

        })
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
            adapter = wordsAdapter
            setHasFixedSize(true)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
        val itemTouchHelper = ItemTouchHelper(object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val deletedWord = wordsAdapter.getItem(position)
                showUndoSnackBar(deletedWord)
                viewModel.deleteWord(deletedWord)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun showUndoSnackBar(deletedWord: Word) {
        val text = viewModel.makeSnackBarText(deletedWord.name)
        snackbar = Snackbar.make(binding.recyclerView, text, Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.undo)) {
                viewModel.insertWord(deletedWord)
            }
        snackbar!!.show()
    }

    private fun slideUpFAB() {
        binding.floatingActionButton.animate()
            .rotation(180f).duration = 500
    }

//    private fun onDeleteClicked() {
//        val items = wordsAdapter.selectedItems()
//        AlertDialog.Builder(requireContext())
//            .setMessage(getString(R.string.do_u_want_to_delete))
//            .setPositiveButton(getString(R.string.ok)) { _, _ ->
////                viewModel.deleteWords(items)
//                wordsAdapter.isShortClickActivated = false
//                onSelectItems(false)
//            }
//            .setNegativeButton(getString(R.string.cancel)) { v, _ ->
//                v.dismiss()
//            }.create().show()
//    }

    private fun directToEditCategory() {
        val action =
            InCategoryFragmentDirections.actionInCategoryFragmentToAddEditCategoryFragment(viewModel.category)
        navigate(action)
    }

    private fun onFloatingActionWordClick() {
        val action =
            InCategoryFragmentDirections.actionInCategoryFragmentToAddEditWordFragment(category = viewModel.category)
        navigate(action)
    }


    override fun onItemClick(vocabulary: Word) {
        val action =
            InCategoryFragmentDirections.actionInCategoryFragmentToAddEditWordFragment(
                vocabulary,
                viewModel.category
            )
        navigate(action)
    }

    override fun onSelectItems(isShortClickActivated: Boolean, selectedItemsCount: Int) {
        binding.apply {
            toolbar.setNavigationIcon(
                if (isShortClickActivated) R.drawable.ic_clear
                else R.drawable.ic_back
            )
            folderName.text =
                if (isShortClickActivated)
                    selectedItemsCount.toString()
                else
                    viewModel.category.mainName
            edit.isVisible = !isShortClickActivated
            deleteCategory.isVisible = isShortClickActivated
            floatingActionButton.isVisible = !isShortClickActivated
        }
    }

    override fun onAudioClicked(word: Word) {
        val newText = word.translations.joinToString(", ")
        textToSpeech.speak(
            newText,
            TextToSpeech.QUEUE_FLUSH,
            null,
            newText
        )
    }

    private fun isLanguageInstalled(): Boolean {
        return true
    }

    override fun onDestroyView() {
        snackbar?.dismiss()
        textToSpeech.shutdown()
        super.onDestroyView()
    }
}