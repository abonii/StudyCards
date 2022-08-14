package abm.co.studycards.ui.vocabulary

import abm.co.studycards.domain.model.LearnOrKnown
import abm.co.studycards.domain.model.ResultWrapper
import abm.co.studycards.domain.model.Word
import abm.co.studycards.domain.usecases.GetUserWordsUseCase
import abm.co.studycards.domain.usecases.UpdateUserWordUseCase
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
    private val getUserWordsUseCase: GetUserWordsUseCase,
    private val updateUserWordUseCase: UpdateUserWordUseCase
) : BaseViewModel() {

    val dispatcher = Dispatchers.IO

    var currentTabType = LearnOrKnown.UNCERTAIN

    private val _stateFlow = MutableStateFlow<VocabularyUiState>(
        VocabularyUiState.Loading
    )
    val stateFlow = _stateFlow.asStateFlow()

    fun initWords(pos: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            //define current type: unknown, known, uncertain
            currentTabType = LearnOrKnown.getType(pos)
            delay(800)
            getUserWordsUseCase(currentTabType).collectLatest {
                when (it) {
                    is ResultWrapper.Error -> {
                        _stateFlow.value = VocabularyUiState.Error(it.res)
                    }
                    ResultWrapper.Loading -> {
                        _stateFlow.value = VocabularyUiState.Loading
                    }
                    is ResultWrapper.Success -> {
                        _stateFlow.value = VocabularyUiState.Success(it.value)
                    }
                }
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
            updateUserWordUseCase.learnType(word)
        }
    }
}

sealed class VocabularyUiState {
    data class Success(val value: List<Word>) : VocabularyUiState()
    data class Error(@StringRes val error: Int) : VocabularyUiState()
    object Loading : VocabularyUiState()
}