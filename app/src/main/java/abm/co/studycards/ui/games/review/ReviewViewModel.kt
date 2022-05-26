package abm.co.studycards.ui.games.review

import abm.co.studycards.domain.model.Word
import abm.co.studycards.util.Constants.ONE_TIME_CYCLE_GAME
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : BaseViewModel() {
    val isRepeat = savedStateHandle.get<Boolean>("isRepeat") ?: false
    private val _words = savedStateHandle.get<Array<Word>>("words")
    var words = _words!!

    fun getFiveWords(): MutableList<Word?> = words.take(ONE_TIME_CYCLE_GAME).toMutableList()
}