package abm.co.studycards.ui.games.select

import abm.co.studycards.anim.Animations
import abm.co.studycards.data.model.LearnOrKnown
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.databinding.FragmentSelectLearnTypeDialogBinding
import abm.co.studycards.util.navigate
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SelectLearnTypeDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentSelectLearnTypeDialogBinding
    private val viewModel: SelectLearnTypeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSelectLearnTypeDialogBinding.inflate(inflater, container, false)
        setBindings()
        setViewModels()

        return binding.root
    }

    private fun setBindings() {
        binding.apply {
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
        lifecycleScope.launchWhenCreated {
            viewModel.apply {
                currentCategory.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        allWordsList.clear()
                        unlearnedWordsList.clear()
                        repeatWordsList.clear()
                        undefinedWordsList.clear()
                        Log.i("qwert", "++${category.id}")
                        snapshot.children.forEach { words ->
                            words.getValue(Word::class.java)
                                ?.let { word ->
                                    Log.i("qwert", word.name + "++")
                                    val currentTime = Calendar.getInstance().timeInMillis
                                    val type = LearnOrKnown.getType(word.learnOrKnown)
                                    if (LearnOrKnown.UNCERTAIN == type || LearnOrKnown.UNKNOWN == type) {
                                        if (word.nextRepeatTime <= currentTime)
                                            repeatWordsList.add(word)
                                        unlearnedWordsList.add(word)
                                    }
                                    if (LearnOrKnown.UNDEFINED == type) {
                                        undefinedWordsList.add(word)
                                    }
                                    allWordsList.add(word)
                                }

                        }
                        allWordsListLive.postValue(allWordsList)
                        repeatWordsLive.postValue(repeatWordsList)
                        undefinedWordsListLive.postValue(undefinedWordsList)
                        unlearnedWordsListLive.postValue(unlearnedWordsList)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }

        }
    }


    private fun setViewModels() {
        viewModel.undefinedWordsListLive.observe(viewLifecycleOwner) {
            val subtitleText = viewModel.getTextForLearnSubtitle(requireContext(), it.size)
            binding.apply {
                learnSubtitle.text = subtitleText
                oneGameSubtitle.text = subtitleText
            }
            viewModel.undefinedWordsList = it
        }

        viewModel.unlearnedWordsListLive.observe(viewLifecycleOwner) {
            val subtitleText = viewModel.getTextForRepeatSubtitle(requireContext(), it.size)
            binding.apply {
                oneGameSubtitle2.text = subtitleText
            }
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
                SelectLearnTypeDialogFragmentDirections.actionSelectLearnTypeDialogFragmentToGuessingFragment(
                    isRepeat = false,
                    words = viewModel.getUnlearnedWordsInTypedArray()
                )
            navigate(action)
        }
    }

    private fun onPairingClicked() {
        if (viewModel.unlearnedWordsList.isNotEmpty()) {
            val action =
                SelectLearnTypeDialogFragmentDirections.actionSelectLearnTypeDialogFragmentToMatchingPairsFragment(
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
                    .actionSelectLearnTypeDialogFragmentToReviewFragment(
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
                    .actionSelectLearnTypeDialogFragmentToToRightOrLeftFragment(
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
                    .actionSelectLearnTypeDialogFragmentToReviewFragment(
                        isRepeat = true,
                        words = viewModel.getRepeatWordsInTypedArray()
                    )
            navigate(action)
        }
    }
}