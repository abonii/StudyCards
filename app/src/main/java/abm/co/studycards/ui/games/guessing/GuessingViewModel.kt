package abm.co.studycards.ui.games.guessing

import abm.co.studycards.data.model.LearnOrKnown
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.util.Constants.ONE_TIME_CYCLE_GAME
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GuessingViewModel @Inject constructor(
    private val repository: ServerCloudRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
    var isClicked: Boolean = false
    val isRepeat = savedStateHandle.get<Boolean>("isRepeat") ?: false
    private val _words = savedStateHandle.get<Array<Word>>("words")
    val wordsAll = _words!!
    val words = wordsAll.take(ONE_TIME_CYCLE_GAME).shuffled()
    var index = 0
    fun getWord() = words[index]
    fun increaseIndex() = index++

    fun getLastWords(): Array<Word> {
        return wordsAll.takeLast(wordsAll.size - ONE_TIME_CYCLE_GAME).toTypedArray()
    }

    fun updateWords() {
        for (w in words) {
            val repeatCount = w.repeatCount
            val updateWord = if (repeatCount >= 2) {
                w.copy(
                    learnOrKnown = LearnOrKnown.KNOWN.getType(),
                    repeatCount = 0
                )
            } else {
                w.copy(
                    nextRepeatTime = nextRepeatTime(repeatCount),
                    repeatCount = repeatCount + 1
                )
            }

            updateWord(updateWord)
        }
    }

    private fun nextRepeatTime(repeatCount: Int): Long {
        val calendar = Calendar.getInstance()
        when (repeatCount) {
            0 -> {
                calendar.add(Calendar.HOUR_OF_DAY, 12)
            }
            1 -> {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            else -> {
                calendar.add(Calendar.HOUR_OF_DAY, 3)
            }
        }
        return calendar.timeInMillis
    }

    private fun updateWord(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateWordRepeatType(word)
        }
    }
}