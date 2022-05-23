package abm.co.studycards.ui.games.matching

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.domain.model.ConfirmText
import abm.co.studycards.databinding.FragmentMatchingPairsBinding
import abm.co.studycards.ui.games.matching.MatchingAdapter.Companion.CORRECT
import abm.co.studycards.ui.games.matching.MatchingAdapter.Companion.NOT_CORRECT
import abm.co.studycards.util.Constants.ONE_TIME_CYCLE_GAME
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.navigate
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchingPairsFragment :
    BaseBindingFragment<FragmentMatchingPairsBinding>(R.layout.fragment_matching_pairs) {
    private val viewModel: MatchingPairsViewModel by viewModels()
    private var adapterTranslatedWords: MatchingAdapter? = null
    private var adapterWords: MatchingAdapter? = null

    override fun initUI(savedInstanceState: Bundle?) {
        (activity as MainActivity).setToolbar(binding.toolbar)
        initRV()
    }

    private fun initRV() {
        adapterWords = MatchingAdapter(::onClick, false).apply {
            items = viewModel.getDefaultWords()
        }
        adapterTranslatedWords = MatchingAdapter(::onClick, true).apply {
            items = viewModel.getDefaultWords()
        }
        binding.run {
            firstRecycler.adapter = adapterTranslatedWords
            firstRecycler.layoutManager = GridLayoutManager(
                requireContext(), ONE_TIME_CYCLE_GAME,
                GridLayoutManager.HORIZONTAL, false
            )
            secondRecycler.adapter = adapterWords
            secondRecycler.layoutManager = GridLayoutManager(
                requireContext(), ONE_TIME_CYCLE_GAME,
                GridLayoutManager.HORIZONTAL, true
            )
        }
    }

    fun onClick(currentItemId: String, isTranslatedWord: Boolean): Boolean? {
        if (isTranslatedWord) {
            viewModel.translatedClickedItem = currentItemId
        } else {
            viewModel.wordClickedItem = currentItemId
        }
        if (currentItemId.isEmpty()) {
            return null
        }
        return checkIfSameWord(isTranslatedWord)
    }

    /**
     * @param isTranslatedWord is just clicked adapter determiner
     * I had a problem, two notifyItemChanged methods intersect.
     * And the latter will not be called.
     * That is why I am using @param isTranslatedWord.
     **/
    private fun checkIfSameWord(isTranslatedWord: Boolean): Boolean? {
        if (viewModel.translatedClickedItem == viewModel.wordClickedItem) {
            bothSideCorrect(isTranslatedWord)
            return true
        } else if (viewModel.translatedClickedItem.isNotEmpty()
            && viewModel.wordClickedItem.isNotEmpty()
        ) {
            onBothSideClicked(isTranslatedWord)
            return false
        }
        return null
    }

    private fun bothSideCorrect(isTranslatedWord: Boolean) {
        viewModel.countOfElements++
        if (isTranslatedWord) {
            adapterWords?.run { notifyItemChanged(selectedItemPos, CORRECT) }
        } else {
            adapterTranslatedWords?.run { notifyItemChanged(selectedItemPos, CORRECT) }
        }
        resetChecking()
        checkIsFinished()
    }

    private fun checkIsFinished() {
        if (viewModel.countOfElements >= viewModel.wordsSize) {
            onFinish()
        }
    }

    private fun onBothSideClicked(isTranslatedWord: Boolean) {
        if (isTranslatedWord) {
            adapterWords?.run { notifyItemChanged(selectedItemPos, NOT_CORRECT) }
        } else {
            adapterTranslatedWords?.run { notifyItemChanged(selectedItemPos, NOT_CORRECT) }
        }
        resetChecking()
    }

    private fun resetChecking() {
        viewModel.translatedClickedItem = ""
        viewModel.wordClickedItem = ""
    }

    private fun onFinish() {
        val action = if (viewModel.isRepeat) {
            MatchingPairsFragmentDirections
                .actionMatchingPairsFragmentToGuessingFragment(
                    isRepeat = true, words = viewModel.words
                )
        } else {
            if (viewModel.words.size >= ONE_TIME_CYCLE_GAME
                && viewModel.getLastWords().isNotEmpty()
            ) {
                MatchingPairsFragmentDirections
                    .actionMatchingPairsFragmentSelf(
                        isRepeat = false, words = viewModel.getLastWords()
                    )
            } else {
                MatchingPairsFragmentDirections
                    .actionGlobalConfirmEndFragment(ConfirmText.FINISH_PAIR)
            }
        }
        navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapterTranslatedWords = null
        adapterWords = null
//        viewModel.translatedClickedItem = ""
//        viewModel.wordClickedItem = ""
    }
}
