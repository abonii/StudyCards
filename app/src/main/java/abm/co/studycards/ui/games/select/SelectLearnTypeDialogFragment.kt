package abm.co.studycards.ui.games.select

import abm.co.studycards.R
import abm.co.studycards.anim.Animations
import abm.co.studycards.databinding.FragmentSelectLearnTypeDialogBinding
import abm.co.studycards.util.base.BaseDialogFragment
import abm.co.studycards.util.navigate
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectLearnTypeDialogFragment :
    BaseDialogFragment<FragmentSelectLearnTypeDialogBinding>(R.layout.fragment_select_learn_type_dialog) {

    private val viewModel: SelectLearnTypeViewModel by viewModels()

    override fun initUI(savedInstanceState: Bundle?) {
        setClickListeners()
        setViewModels()
    }

    private fun setClickListeners() {
        binding.run {
            learn.setOnClickListener {
                onLearnClicked()
            }
            repeat.setOnClickListener {
                onRepeatClicked()
            }
            review.setOnClickListener {
                onReviewClicked()
            }
            oneTypeGame.setOnClickListener {
                onOneTypeGameClicked()
            }
            pairing.setOnClickListener {
                onPairingClicked()
            }
            guessing.setOnClickListener {
                onGuessingClicked()
            }
        }
    }


    private fun setViewModels() {
        viewModel.undefinedWordsListLive.observe(viewLifecycleOwner) {
            val subtitleText = viewModel.getTextForLearnSubtitle(requireContext(), it.size)
            binding.run {
                learnSubtitle.text = subtitleText
                oneGameSubtitle.text = subtitleText
            }
            viewModel.undefinedWordsList = it
        }

        viewModel.unlearnedWordsListLive.observe(viewLifecycleOwner) {
            val subtitleText = viewModel.getTextForRepeatSubtitle(requireContext(), it.size)
            binding.oneGameSubtitle2.text = subtitleText
            viewModel.unlearnedWordsList = it
        }
        viewModel.allWordsListLive.observe(viewLifecycleOwner) {
            viewModel.allWordsList = it
        }
        viewModel.repeatWordsLive.observe(viewLifecycleOwner) {
            viewModel.repeatWordsList = it
            binding.repeatSubtitle.text =
                viewModel.getTextForRepeatSubtitle(requireContext(), it.size)
        }
    }

    private fun onOneTypeGameClicked() {
        val animations = Animations()
        if (binding.oneGame.isVisible) {
            animations.collapse(binding.oneGame)
        } else
            animations.expand(binding.oneGame)
    }

    private fun onGuessingClicked() {
        if (viewModel.unlearnedWordsList.isNotEmpty()) {
            val action =
                SelectLearnTypeDialogFragmentDirections.actionSelectGamesTypeDialogFragmentToGuessingFragment(
                    isRepeat = false,
                    words = viewModel.getUnlearnedWordsInTypedArray()
                )
            navigate(action)
        }
    }

    private fun onPairingClicked() {
        if (viewModel.unlearnedWordsList.isNotEmpty()) {
            val action =
                SelectLearnTypeDialogFragmentDirections.actionSelectGamesTypeDialogFragmentToMatchingPairsFragment(
                    isRepeat = false,
                    words = viewModel.getUnlearnedWordsInTypedArray()
                )
            navigate(action)
        }
    }

    private fun onReviewClicked() {
        if (viewModel.allWordsList.isNotEmpty()) {
            val action =
                SelectLearnTypeDialogFragmentDirections
                    .actionSelectGamesTypeDialogFragmentToReviewFragment(
                        isRepeat = false,
                        words = viewModel.getAllWordsInTypedArray()
                    )
            navigate(action)
        }
    }

    private fun onLearnClicked() {
        if (viewModel.undefinedWordsList.isNotEmpty()) {
            val action =
                SelectLearnTypeDialogFragmentDirections
                    .actionSelectGamesTypeDialogFragmentToToRightOrLeftFragment(
                        viewModel.categoryName,
                        viewModel.getLearnWordsInTypedArray()
                    )
            navigate(action)
        }
    }

    private fun onRepeatClicked() {
        if (viewModel.repeatWordsList.isNotEmpty()) {
            val action =
                SelectLearnTypeDialogFragmentDirections
                    .actionSelectGamesTypeDialogFragmentToReviewFragment(
                        isRepeat = true,
                        words = viewModel.getRepeatWordsInTypedArray()
                    )
            navigate(action)
        }
    }
}