package abm.co.studycards.ui.vocabulary

import abm.co.studycards.R
import abm.co.studycards.data.model.LearnOrKnown
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.util.Constants.CATEGORIES_REF
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.core.App
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class VocabularyViewModel @Inject constructor(
    @Named(CATEGORIES_REF)
    var categoriesDbRef: DatabaseReference,
    private val repository: ServerCloudRepository
) : BaseViewModel() {

    val dispatcher = Dispatchers.IO

    var currentTabType = LearnOrKnown.UNCERTAIN
    var firstBtnType = LearnOrKnown.KNOWN
    var secondBtnType = LearnOrKnown.UNKNOWN

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
                                        it.getValue(Word::class.java)
                                            ?.let { word ->
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
                    delay(800)
                    if (items.size == 0) {
                        _stateFlow.value = VocabularyUiState.Error(App.instance.getString(R.string.empty))
                    } else _stateFlow.value = VocabularyUiState.Success(items)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _stateFlow.value = VocabularyUiState.Error(error.message)
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
            repository.updateWordLearnType(word)
        }
    }


}

sealed class VocabularyUiState {
    data class Success(val value: List<Word>) : VocabularyUiState()
    data class Error(val error: String) : VocabularyUiState()
    object Loading : VocabularyUiState()
}
