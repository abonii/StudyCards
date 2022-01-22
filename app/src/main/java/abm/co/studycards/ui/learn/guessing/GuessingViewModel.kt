package abm.co.studycards.ui.learn.guessing

import abm.co.studycards.data.model.LearnOrKnown
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.util.Constants
import abm.co.studycards.util.Constants.oneTimeCyclingForGame
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class GuessingViewModel @Inject constructor(
    @Named(Constants.CATEGORIES_REF) private val categoriesDbRef: DatabaseReference,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
    var isClicked: Boolean = false
    val isRepeat = savedStateHandle.get<Boolean>("isRepeat") ?: false
    private val _words = savedStateHandle.get<Array<Word>>("words")
    val wordsAll = _words!!
    val words = wordsAll.take(oneTimeCyclingForGame).shuffled()
    var index = 0
    fun getWord() = words[index]
    fun increaseIndex() = index++

    fun getLastWords(): Array<Word> {
        return wordsAll.takeLast(wordsAll.size - oneTimeCyclingForGame).toTypedArray()
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
                calendar.add(Calendar.HOUR_OF_DAY, 1)
            }
            1 -> {
                calendar.add(Calendar.HOUR_OF_DAY, 2)
            }
            else -> {
                calendar.add(Calendar.HOUR_OF_DAY, 3)
            }
        }
        return calendar.timeInMillis
    }

    private fun updateWord(copy: Word) {
        viewModelScope.launch {
            updateWordLearnType(copy)
        }
    }

    private fun updateWordLearnType(word: Word) {
        launchIO {
            categoriesDbRef.child(word.categoryID)
                .child(Constants.WORDS_REF)
                .child(word.wordId)
                .updateChildren(mapOf(
                    Word.LEARN_OR_KNOWN to word.learnOrKnown,
                    Word.REPEAT_COUNT to word.repeatCount,
                    Word.NEXT_REPEAT_TIME to word.nextRepeatTime))
        }
    }
}