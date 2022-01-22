package abm.co.studycards.ui.learn.review

import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.util.Constants.oneTimeCyclingForGame
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {
    val isRepeat = savedStateHandle.get<Boolean>("isRepeat") ?: false
    private val _words = savedStateHandle.get<Array<Word>>("words")
    var words = _words!!

    fun getFiveWords(): List<Word> {
        return words.take(oneTimeCyclingForGame)
    }
}