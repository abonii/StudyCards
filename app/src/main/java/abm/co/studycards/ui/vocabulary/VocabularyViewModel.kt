package abm.co.studycards.ui.vocabulary

import abm.co.studycards.data.model.LearnOrKnown
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.repository.FirebaseRepository
import abm.co.studycards.util.Constants
import abm.co.studycards.util.base.BaseViewModel
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class VocabularyViewModel @Inject constructor(
    @Named(Constants.CATEGORIES_REF) val categoriesDbRef: DatabaseReference,
) : BaseViewModel() {
    private val repository: FirebaseRepository = FirebaseRepository(categoriesDbRef)

    fun changeType(word: Word, type: LearnOrKnown) {
        when (type) {
            LearnOrKnown.KNOWN ->{
                updateWordLearnType(word.copy(learnOrKnown = LearnOrKnown.KNOWN.getType()))
            }
            LearnOrKnown.UNCERTAIN -> {
                updateWordLearnType(word.copy(learnOrKnown = LearnOrKnown.UNCERTAIN.getType()))
            }
            else -> {
                updateWordLearnType(word.copy(learnOrKnown = LearnOrKnown.UNKNOWN.getType()))
            }
        }
    }

    private fun updateWordLearnType(word:Word){
        launchIO {
            repository.updateWordLearnType(word)
        }
    }


}