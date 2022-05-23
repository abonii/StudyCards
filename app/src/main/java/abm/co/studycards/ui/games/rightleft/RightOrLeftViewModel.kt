package abm.co.studycards.ui.games.rightleft

import abm.co.studycards.domain.model.LearnOrKnown
import abm.co.studycards.domain.Prefs
import abm.co.studycards.domain.model.Word
import abm.co.studycards.domain.repository.ServerCloudRepository
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.yuyakaido.android.cardstackview.Direction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RightOrLeftViewModel @Inject constructor(
    private val firebaseRepository: ServerCloudRepository,
    savedStateHandle: SavedStateHandle,
    prefs: Prefs
) : BaseViewModel() {

    val category = savedStateHandle.get<String>("category")!!
    private val _words = savedStateHandle.get<Array<Word>>("words")
    val words = _words!!.toList()
    var cardPosition: Int = 0
    val targetLang = prefs.getTargetLanguage()

    fun updateWord(word: Word, direction: Direction?) {
        when (direction) {
            Direction.Right -> {
                updateWord(word.copy(learnOrKnown = LearnOrKnown.KNOWN.getType()))
            }
            Direction.Bottom -> {
                updateWord(word.copy(learnOrKnown = LearnOrKnown.UNCERTAIN.getType()))
            }
            else -> {
                updateWord(word.copy(learnOrKnown = LearnOrKnown.UNKNOWN.getType()))
            }
        }
    }

    private fun updateWord(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseRepository.updateWord(word)
        }
    }
}