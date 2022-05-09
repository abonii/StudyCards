package abm.co.studycards.ui.vocabulary

import abm.co.studycards.R
import abm.co.studycards.data.model.LearnOrKnown
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.firebaseError
import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabularyViewModel @Inject constructor(
    private val firebaseRepository: ServerCloudRepository
) : BaseViewModel() {

    val dispatcher = Dispatchers.IO
    private val categoriesDbRef = firebaseRepository.getCategoriesReference()

    var currentTabType = LearnOrKnown.UNCERTAIN
    var firstBtnType = LearnOrKnown.KNOWN
    var secondBtnType = LearnOrKnown.UNKNOWN

    private var firstTime = true

    private val _stateFlow = MutableStateFlow<VocabularyUiState>(
        VocabularyUiState.Loading
    )
    val stateFlow = _stateFlow.asStateFlow()

    fun initWords(pos: Int) {
        setTabSettings(pos)
        categoriesDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch(dispatcher) {
                    val items = mutableListOf<Word>()
                    snapshot.children.forEach { categories ->
                        categories.children.forEach { categoryId ->
                            if (categoryId.key?.isBlank() != true) {
                                categoryId.children.forEach {
                                    try {
                                        it.getValue(Word::class.java)?.let { word ->
                                            if (LearnOrKnown.getType(word.learnOrKnown) == currentTabType) {
                                                items.add(word)
                                            }
                                        }
                                    } catch (e: DatabaseException) {
                                        categoryId.ref.removeValue()
                                    }
                                }
                            }
                        }
                    }
                    if (firstTime) {
                        delay(800)
                        firstTime = false
                    }
                    if (items.size == 0) {
                        _stateFlow.value =
                            VocabularyUiState.Error(R.string.empty)
                    } else _stateFlow.value = VocabularyUiState.Success(items)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _stateFlow.value = VocabularyUiState.Error(firebaseError(error.code))
            }

        })
    }

    private fun setTabSettings(pos: Int) {
        currentTabType = LearnOrKnown.getType(pos)
        when (currentTabType) {
            LearnOrKnown.KNOWN -> {
                firstBtnType = LearnOrKnown.UNKNOWN
                secondBtnType = LearnOrKnown.UNCERTAIN
            }
            LearnOrKnown.UNCERTAIN -> {
                firstBtnType = LearnOrKnown.UNKNOWN
                secondBtnType = LearnOrKnown.KNOWN
            }
            else -> {
                firstBtnType = LearnOrKnown.UNCERTAIN
                secondBtnType = LearnOrKnown.KNOWN
            }
        }
    }

    fun changeType(word: Word, type: LearnOrKnown) {
        when (type) {
            LearnOrKnown.KNOWN -> {
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

    private fun updateWordLearnType(word: Word) {
        viewModelScope.launch(dispatcher) {
            firebaseRepository.updateWordLearnType(word)
        }
    }


}

sealed class VocabularyUiState {
    data class Success(val value: List<Word>) : VocabularyUiState()
    data class Error(@StringRes val error: Int) : VocabularyUiState()
    object Loading : VocabularyUiState()
}
