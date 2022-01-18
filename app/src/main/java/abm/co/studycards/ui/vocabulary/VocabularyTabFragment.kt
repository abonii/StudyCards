package abm.co.studycards.ui.vocabulary

import abm.co.studycards.R
import abm.co.studycards.data.model.MyButton
import abm.co.studycards.data.model.MyButtonClickListener
import abm.co.studycards.databinding.FragmentVocabularyTabBinding
import abm.co.studycards.helpers.SwipeController
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.getMyColor
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class VocabularyTabFragment(vocabularyTabType: VocabularyTabType) :
    BaseBindingFragment<FragmentVocabularyTabBinding>(R.layout.fragment_vocabulary_tab) {

    private lateinit var adapterV: VocabularyAdapter
    private val viewModel: VocabularyViewModel by viewModels()

    override fun initViews(savedInstanceState: Bundle?) {
        adapterV = VocabularyAdapter()
//        viewModel.doubtedWords().observe(requireActivity(), {
//            if (it != adapterV.items)
//                adapterV.items = it.toMutableList()
//            it.isNotEmpty().let { b ->
//                binding.apply {
//                    recyclerView.isVisible = b
//                    text.isVisible = !b
//                }
//            }
//        })
        setUpRecyclerView()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setUpRecyclerView() {
        object : SwipeController(requireContext(), binding.recyclerView, 200) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(
                    MyButton(getString(R.string.know), 0, getMyColor(R.color.green),
                        object : MyButtonClickListener {
                            override fun onClick(pos: Int) {
                                val removed = adapterV.deleteItem(pos)
//                                viewModel.onKnownClicked(removed)
                            }

                        })
                )
                buffer.add(
                    MyButton(getString(R.string.unknown), 0, getMyColor(R.color.red),
                        object : MyButtonClickListener {
                            override fun onClick(pos: Int) {
                                val removed = adapterV.deleteItem(pos)
//                                viewModel.onUnknownClicked(removed)
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