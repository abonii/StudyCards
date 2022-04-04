package abm.co.studycards.ui.games.matching

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.data.model.ConfirmText
import abm.co.studycards.databinding.FragmentMatchingPairsBinding
import abm.co.studycards.util.Constants.ONE_TIME_CYCLE_GAME
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.navigate
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.animation.doOnEnd
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchingPairsFragment :
    BaseBindingFragment<FragmentMatchingPairsBinding>(R.layout.fragment_matching_pairs) {
    private val viewModel: MatchingPairsViewModel by viewModels()
    private var adapterTranslatedWords: MatchingAdapter? = null
    private var adapterWords: MatchingAdapter? = null

    override fun initUI(savedInstanceState: Bundle?) {
        (activity as MainActivity).setToolbar(binding.toolbar, findNavController())
        adapterWords = MatchingAdapter(::onClick, false).apply {
            words = viewModel.getWords()
        }
        adapterTranslatedWords = MatchingAdapter(::onClick, true).apply {
            words = viewModel.getWords()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
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

    fun onClick(
        currentItemId: String,
        isTranslatedWord: Boolean,
    ): Boolean {
        if (isTranslatedWord) {
            viewModel.translatedClickedItem = currentItemId
        } else {
            viewModel.wordClickedItem = currentItemId
        }
        return checkIfSameWord()
    }

    private fun checkIfSameWord(): Boolean {
        if (viewModel.translatedClickedItem == viewModel.wordClickedItem) {
            adapterTranslatedWords?.currentItemCard?.let { disappearCards(it) }
            adapterWords?.currentItemCard?.let { disappearCards(it) }
            viewModel.countOfElements++
            viewModel.translatedClickedItem = ""
            viewModel.wordClickedItem = ""
            if (viewModel.countOfElements >= viewModel.wordsSize) {
                onFinish()
            }
        } else if (viewModel.translatedClickedItem != "" && viewModel.wordClickedItem != "") {
            onBothSideClicked()
            return false
        }
        return true
    }

    private fun onBothSideClicked() {
        val shake: Animation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
        adapterWords?.run {
            currentItemCard?.run {
                strokeColor = Color.TRANSPARENT
                startAnimation(shake)
            }
            lastItemSelectedPos = -1
            selectedItemPos = -1
        }
        adapterTranslatedWords?.run {
            currentItemCard?.run {
                strokeColor = Color.TRANSPARENT
                startAnimation(shake)
            }
            lastItemSelectedPos = -1
            selectedItemPos = -1
        }
        viewModel.translatedClickedItem = ""
        viewModel.wordClickedItem = ""
    }

    private fun onFinish() {
        val action = if (viewModel.isRepeat) {
            MatchingPairsFragmentDirections
                .actionMatchingPairsFragmentToGuessingFragment(
                    isRepeat = true,
                    words = viewModel.words
                )

        } else {
            if (viewModel.words.size >= ONE_TIME_CYCLE_GAME && viewModel.getLastWords()
                    .isNotEmpty()
            ) {
                MatchingPairsFragmentDirections
                    .actionMatchingPairsFragmentSelf(
                        isRepeat = false,
                        words = viewModel.getLastWords()
                    )
            } else {
                MatchingPairsFragmentDirections
                    .actionGlobalConfirmEndFragment(ConfirmText.FINISH_PAIR)
            }
        }
        navigate(action)
    }

    private fun disappearCards(card: View) {
        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            card,
            PropertyValuesHolder.ofFloat("scaleX", 0f),
            PropertyValuesHolder.ofFloat("scaleY", 0f)
        )
        scaleDown.duration = 600
        scaleDown.start()
        scaleDown.doOnEnd {
            card.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapterTranslatedWords = null
        adapterWords = null
        viewModel.translatedClickedItem = ""
        viewModel.wordClickedItem = ""
    }
}
