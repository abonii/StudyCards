package abm.co.studycards.ui.vocabulary

import abm.co.studycards.R
import abm.co.studycards.domain.model.LearnOrKnown
import abm.co.studycards.domain.model.Word
import abm.co.studycards.domain.repository.ServerCloudRepository
import abm.co.studycards.util.base.BaseViewModel
import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabularyViewModel @Inject constructor(
    private val firebaseRepository: ServerCloudRepository
) : BaseViewModel() {

    val dispatcher = Dispatchers.IO

    var currentTabType = LearnOrKnown.UNCERTAIN

    private val _stateFlow = MutableStateFlow<VocabularyUiState>(
        VocabularyUiState.Loading
    )
    val stateFlow = _stateFlow.asStateFlow()

    fun initWords(pos: Int) {
        viewModelScope.launch(dispatcher) {
            setTabSettings(pos)
            delay(600)
            firebaseRepository.fetchUserWords().collectLatest { words ->
                val tabWords =
                    words.filter { LearnOrKnown.getType(it.learnOrKnown) == currentTabType }
                if (tabWords.isEmpty()) {
                    _stateFlow.value = VocabularyUiState.Error(R.string.empty)
                } else _stateFlow.value = VocabularyUiState.Success(tabWords)
            }
        }
    }

    private fun setTabSettings(pos: Int) {
        currentTabType = LearnOrKnown.getType(pos)
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
