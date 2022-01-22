package abm.co.studycards.ui.learn.rightleft

import abm.co.studycards.data.model.LearnOrKnown
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.util.Constants
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.database.DatabaseReference
import com.yuyakaido.android.cardstackview.Direction
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class RightOrLeftViewModel @Inject constructor(
    @Named(Constants.CATEGORIES_REF) private val categoriesDbRef: DatabaseReference,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val category = savedStateHandle.get<String>("category")!!
    private val _words = savedStateHandle.get<Array<Word>>("words")
    val words = _words!!.toList()
    var cardPosition: Int = 0

    fun updateWord(word: Word, direction: Direction?) {
        when (direction) {
            Direction.Left -> {
                updateWord(word.copy(learnOrKnown = LearnOrKnown.UNKNOWN.getType()))
            }
            Direction.Right -> {
                updateWord(word.copy(learnOrKnown = LearnOrKnown.KNOWN.getType()))
            }
            Direction.Bottom -> {
                updateWord(word.copy(learnOrKnown = LearnOrKnown.UNCERTAIN.getType()))
            }
            else -> {
            }
        }
    }

    private fun updateWord(word: Word) {
        launchIO {
            categoriesDbRef.child(word.categoryID)
                .child(Constants.WORDS_REF)
                .child(word.wordId)
                .updateChildren(mapOf(Word.LEARN_OR_KNOWN to word.learnOrKnown))
        }
    }
}