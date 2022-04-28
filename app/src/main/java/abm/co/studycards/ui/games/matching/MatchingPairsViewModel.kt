package abm.co.studycards.ui.games.matching

import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.util.Constants.ONE_TIME_CYCLE_GAME
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MatchingPairsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
    var translatedClickedItem: String = ""
    var wordClickedItem: String = ""
    var countOfElements = 0
    val isRepeat = savedStateHandle.get<Boolean>("isRepeat") ?: false
    private val _words = savedStateHandle.get<Array<Word>>("words")
    val words = _words!!
    fun getDefaultWords() = words.take(ONE_TIME_CYCLE_GAME).shuffled().map {
        WordMatching(it, isSelected = false, isSelectedCorrect = null)
    }
    val wordsSize = words.size

    fun getLastWords(): Array<Word> {
        return words.takeLast(words.size - ONE_TIME_CYCLE_GAME).toTypedArray()
    }

}