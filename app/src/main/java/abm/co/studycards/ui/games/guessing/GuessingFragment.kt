package abm.co.studycards.ui.games.guessing

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.data.model.ConfirmText
import abm.co.studycards.databinding.FragmentGuessingBinding
import abm.co.studycards.util.Constants.ONE_TIME_CYCLE_GAME
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.changeBackgroundChangesAndFlip
import abm.co.studycards.util.flipInCard
import abm.co.studycards.util.flipOutCard
import abm.co.studycards.util.navigate
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GuessingFragment : BaseBindingFragment<FragmentGuessingBinding>(R.layout.fragment_guessing) {

    private val viewModel: GuessingViewModel by viewModels()
    private lateinit var adapter: OptionsAdapter

    override fun initUI(savedInstanceState: Bundle?) {
        (activity as MainActivity).setToolbar(binding.toolbar, findNavController())
        adapter = OptionsAdapter(::onClick)
        binding.apply {
            toolbar.setNavigationIcon(R.drawable.ic_back)
            word.text = viewModel.getWord().name
            recyclerview.layoutManager = GridLayoutManager(
                requireContext(), ONE_TIME_CYCLE_GAME,
                GridLayoutManager.HORIZONTAL, false
            )
            recyclerview.adapter = adapter.apply {
                words = viewModel.words
            }
        }
    }

    fun onClick(view: View, currentItemId: String) {
        if (!viewModel.isClicked) {
            if (viewModel.getWord().wordId == currentItemId) {
                onAnsweredCorrect(view)
            } else {
                onAnsweredWrong(view)
            }
            viewModel.isClicked = true
        }
    }

    private fun checkIfLast(): Boolean {
        if (viewModel.index + 1 >= viewModel.words.size) {
            onFinish()
            return true
        }
        return false
    }

    private fun onFinish() {
        val action = if (viewModel.isRepeat) {
            viewModel.updateWords()
            if (viewModel.wordsAll.size > ONE_TIME_CYCLE_GAME) {
                GuessingFragmentDirections
                    .actionGuessingFragmentToReviewFragment(
                        isRepeat = true,
                        words = viewModel.getLastWords()
                    )
            } else {
                GuessingFragmentDirections
                    .actionGlobalConfirmEndFragment(ConfirmText.FINISH_REPEAT)
            }

        } else {
            if (viewModel.wordsAll.size >= ONE_TIME_CYCLE_GAME && viewModel.getLastWords()
                    .isNotEmpty()
            ) {
                GuessingFragmentDirections
                    .actionGuessingFragmentSelf(isRepeat = false, words = viewModel.getLastWords())
            } else {
                GuessingFragmentDirections
                    .actionGlobalConfirmEndFragment(ConfirmText.FINISH_GUESS)
            }
        }
        navigate(action)
    }

    //change background color of clicked item for same time
    private fun onAnsweredWrong(view: View) {
        view.changeBackgroundChangesAndFlip(Color.RED).apply {
            doOnEnd {
                adapter.words = viewModel.words
                viewModel.isClicked = false
            }

        }
    }

    //change background color of clicked item for same time
    private fun onAnsweredCorrect(view: View) {
        view.changeBackgroundChangesAndFlip(Color.GREEN).doOnEnd {
            if (!checkIfLast()) {
                onBackgroundColorChanged()
            }
            viewModel.isClicked = false
        }
    }

    //Flipping out the question card
    private fun onBackgroundColorChanged() {
        binding.card.flipOutCard().doOnEnd {
            flipOutCard()
        }
        flipOutRecyclerView()
    }

    //change to next question && flips in question card
    private fun flipOutCard() {
        viewModel.increaseIndex()
        binding.word.text = viewModel.getWord().name
        binding.card.flipInCard()
    }

    //Flips all elements inside the adapter
    private fun flipOutRecyclerView() {
        for (i in 0 until adapter.views.size) {
            if (i == adapter.views.size - 1) {
                adapter.views[i].flipOutCard().doOnEnd {
                    flipInRecyclerView()
                }
            } else adapter.views[i].flipOutCard()
        }
    }

    private fun flipInRecyclerView() {
        adapter.words = viewModel.words
        for (j in 0 until adapter.views.size) {
            adapter.views[j].flipInCard()
        }
    }
}