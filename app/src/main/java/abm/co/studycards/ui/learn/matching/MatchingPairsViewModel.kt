package abm.co.studycards.ui.learn.matching

import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.util.Constants.oneTimeCyclingForGame
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MatchingPairsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    var translatedClickedItem: String = ""
    var wordClickedItem: String = ""
    var countOfElements = 0
    val isRepeat = savedStateHandle.get<Boolean>("isRepeat") ?: false
    private val _words = savedStateHandle.get<Array<Word>>("words")
    val words = _words!!
    val wordsSize = getWords().size
    fun getWords() = words.take(oneTimeCyclingForGame).shuffled()
    fun getLastWords(): Array<Word> {
        return words.takeLast(words.size - oneTimeCyclingForGame).toTypedArray()
    }

}