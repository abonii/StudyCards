package abm.co.studycards.ui.learn.rightleft

import abm.co.studycards.MainActivity
import abm.co.studycards.R
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.databinding.FragmentToRightOrLeftBinding
import abm.co.studycards.ui.learn.confirmend.ConfirmText
import abm.co.studycards.util.base.BaseBindingFragment
import abm.co.studycards.util.navigate
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuyakaido.android.cardstackview.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class ToRightOrLeftFragment :
    BaseBindingFragment<FragmentToRightOrLeftBinding>(R.layout.fragment_to_right_or_left),
    CardStackListener, CardStackAdapter.OnClick {

    private val viewModel: RightOrLeftViewModel by viewModels()
    private lateinit var cardAdapter: CardStackAdapter
    private lateinit var cardLayoutManager: CardStackLayoutManager
    private lateinit var textToSpeech: TextToSpeech

    override fun initUI(savedInstanceState: Bundle?) {
        (activity as MainActivity).setToolbar(binding.toolbar, findNavController())
        setTextToSpeech()
        cardAdapter = CardStackAdapter(this)
        cardLayoutManager = CardStackLayoutManager(requireContext(), this)
        binding.apply {
            folderName.text = viewModel.category.uppercase()
            cardStack.layoutManager = cardLayoutManager
            cardStack.adapter = cardAdapter
        }
        cardLayoutManager.apply {
            setStackFrom(StackFrom.Top)
            setVisibleCount(3)
            setDirections(listOf(Direction.Right, Direction.Left, Direction.Bottom))
            setCanScrollHorizontal(true)
            setCanScrollVertical(true)
            setTranslationInterval(4.0f)
            setScaleInterval(0.95f)
            setSwipeThreshold(0.2f)
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            setMaxDegree(20.0f)
        }

        cardAdapter.words = viewModel.words
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


    override fun onCardDragging(direction: Direction?, ratio: Float) {
        overlayVisibility(View.INVISIBLE)
    }

    override fun onCardRewound() {}

    override fun onCardCanceled() {}

    override fun onCardSwiped(direction: Direction?) {
        val word = cardAdapter.words[viewModel.cardPosition]
        viewModel.updateWord(word, direction)
        isLastElement()
    }

    private fun isLastElement() {
        if (viewModel.cardPosition == cardAdapter.itemCount - 1) {
            val action = ToRightOrLeftFragmentDirections
                .actionGlobalConfirmEndFragment(ConfirmText.FINISH_REVIEW)
            navigate(action)
        }
    }

    override fun onCardAppeared(view: View?, position: Int) {
        viewModel.cardPosition = position
    }

    override fun onCardDisappeared(view: View?, position: Int) {}

    private fun overlayVisibility(myVisibility: Int) {
        binding.myBottomOverlay.apply {
            visibility = myVisibility
        }
        binding.myLeftOverlay.apply {
            visibility = myVisibility
        }
        binding.myRightOverlay.apply {
            visibility = myVisibility
        }
    }

    override fun onClick() {
        overlayVisibility(View.VISIBLE)
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

    override fun onDestroyView() {
        textToSpeech.shutdown()
        super.onDestroyView()
    }
}
//const val TAG = "ToRightOrLeft"