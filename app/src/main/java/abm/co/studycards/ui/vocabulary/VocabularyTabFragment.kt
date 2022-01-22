package abm.co.studycards.ui.vocabulary

import abm.co.studycards.R
import abm.co.studycards.data.model.LearnOrKnown
import abm.co.studycards.data.model.MyButton
import abm.co.studycards.data.model.MyButtonClickListener
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.databinding.FragmentVocabularyTabBinding
import abm.co.studycards.helpers.SwipeController
import abm.co.studycards.util.base.BaseBindingFragment
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class VocabularyTabFragment :
    BaseBindingFragment<FragmentVocabularyTabBinding>(R.layout.fragment_vocabulary_tab) {


    companion object {
        private const val TAB_POSITION = "TAB_POSITION"

        @JvmStatic
        fun newInstance(fragmentPosition: Int) = VocabularyTabFragment().apply {
            arguments = bundleOf(TAB_POSITION to fragmentPosition)
        }
    }

    private var tabType = LearnOrKnown.UNCERTAIN
    private var firstBtnType = LearnOrKnown.KNOWN
    private var secondBtnType = LearnOrKnown.UNKNOWN
    private val adapterV: VocabularyAdapter = VocabularyAdapter()
    private val viewModel: VocabularyViewModel by viewModels()

    override fun initViews(savedInstanceState: Bundle?) {
        tabType = LearnOrKnown.getType(arguments?.getInt(TAB_POSITION) ?: 0)
        when (tabType) {
            LearnOrKnown.KNOWN -> {
                firstBtnType = LearnOrKnown.UNKNOWN
                secondBtnType = LearnOrKnown.UNCERTAIN
            }
            LearnOrKnown.UNCERTAIN -> {
                firstBtnType = LearnOrKnown.UNKNOWN
                secondBtnType = LearnOrKnown.KNOWN
            }
            else -> {
                firstBtnType = LearnOrKnown.UNCERTAIN
                secondBtnType = LearnOrKnown.KNOWN
            }
        }
        viewModel.categoriesDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lifecycleScope.launch {
                    val items = mutableListOf<Word>()
                    lifecycleScope.launch {
                        snapshot.children.forEach { categories ->
                            categories.children.forEach { categoryId ->
                                lifecycleScope.launch {
                                    if (categoryId.key?.isBlank() == true) {
                                        categoryId.children.forEach {
                                            it.getValue(Word::class.java)
                                                ?.let { word ->
                                                    if (LearnOrKnown.getType(word.learnOrKnown) == tabType) {
                                                        items.add(word)
                                                    }
                                                }
                                        }
                                    }
                                }
                            }
                        }
                    }.join()
                    adapterV.submitList(items)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showLog(error.message, "InCategoryWordRef")
            }

        })
        setUpRecyclerView()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setUpRecyclerView() {
        object : SwipeController(requireContext(), binding.recyclerView, 200) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>,
            ) {
                buffer.add(
                    MyButton(secondBtnType.getName(requireContext()),
                        0,
                        secondBtnType.getColor(requireContext()),
                        object : MyButtonClickListener {
                            override fun onClick(pos: Int) {
                                viewModel.changeType(adapterV.getItem(pos), secondBtnType)
                            }

                        })
                )
                buffer.add(
                    MyButton(firstBtnType.getName(requireContext()),
                        0,
                        firstBtnType.getColor(requireContext()),
                        object : MyButtonClickListener {
                            override fun onClick(pos: Int) {
                                viewModel.changeType(adapterV.getItem(pos), firstBtnType)
                            }
                        })
                )
            }
        }
        binding.apply {
            recyclerView.apply {
                adapter = adapterV
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
    }
}