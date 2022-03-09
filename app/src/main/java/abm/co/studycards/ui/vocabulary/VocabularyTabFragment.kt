package abm.co.studycards.ui.vocabulary

import abm.co.studycards.R
import abm.co.studycards.data.model.MyButton
import abm.co.studycards.data.model.MyButtonClickListener
import abm.co.studycards.databinding.FragmentVocabularyTabBinding
import abm.co.studycards.helpers.SwipeController
import abm.co.studycards.util.Constants.VOCABULARY_TAB_POSITION
import abm.co.studycards.util.base.BaseBindingFragment
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class VocabularyTabFragment :
    BaseBindingFragment<FragmentVocabularyTabBinding>(R.layout.fragment_vocabulary_tab) {

    companion object {
        @JvmStatic
        fun newInstance(fragmentPosition: Int) = VocabularyTabFragment().apply {
            arguments = bundleOf(VOCABULARY_TAB_POSITION to fragmentPosition)
        }
    }

    private val adapterV: VocabularyAdapter = VocabularyAdapter()
    private val viewModel: VocabularyViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            viewModel.stateFlow.collectLatest {
                when (it) {
                    is VocabularyUiState.Error -> {
                        binding.text.text = "Error occurred"
                    }
                    VocabularyUiState.Loading -> {
                        binding.recyclerView.isVisible = false
                    }
                    is VocabularyUiState.Success -> {
                        binding.recyclerView.isVisible = true
                        adapterV.submitList(it.value)
                    }
                }
            }
        }
        viewModel.initWords(arguments?.getInt(VOCABULARY_TAB_POSITION) ?: 0)
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
                    MyButton(viewModel.secondBtnType.getName(requireContext()),
                        0,
                        viewModel.secondBtnType.getColor(requireContext()),
                        object : MyButtonClickListener {
                            override fun onClick(pos: Int) {
                                viewModel.changeType(adapterV.getItem(pos), viewModel.secondBtnType)
                            }

                        })
                )
                buffer.add(
                    MyButton(viewModel.firstBtnType.getName(requireContext()),
                        0,
                        viewModel.firstBtnType.getColor(requireContext()),
                        object : MyButtonClickListener {
                            override fun onClick(pos: Int) {
                                viewModel.changeType(adapterV.getItem(pos), viewModel.firstBtnType)
                            }
                        })
                )
            }
        }
        binding.recyclerView.adapter = adapterV
    }
}
