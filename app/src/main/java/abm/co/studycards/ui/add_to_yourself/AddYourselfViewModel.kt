package abm.co.studycards.ui.add_to_yourself

import abm.co.studycards.data.model.WordX
import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.util.Constants.CATEGORIES_REF
import abm.co.studycards.util.Constants.WORDS_REF
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.firebaseError
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddYourselfViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val firebaseRepository: ServerCloudRepository
) : BaseViewModel() {

    private val dispatcher = Dispatchers.IO
    private val exploreDbRef = firebaseRepository.getExploreReference()

    private val _stateFlow = MutableStateFlow<AddYourselfUiState>(AddYourselfUiState.Loading)
    val stateFlow = _stateFlow.asStateFlow()

    private val _isAllSelected = MutableStateFlow(true)
    val isAllSelected = _isAllSelected.asStateFlow()

    private val _sharedFlow = MutableSharedFlow<AddYourselfEventChannel>()
    val sharedFlow = _sharedFlow.asSharedFlow()

    var category = savedStateHandle.get<Category>("category")!!
    private var setId = savedStateHandle.get<String>("set_id")!!

    val selectedWords: MutableList<WordX> = ArrayList()

    fun isSelectedAllWords() = selectedWords.count { it.isChecked } == selectedWords.size

    init {
        val wordsRef = exploreDbRef.child(setId).child(CATEGORIES_REF).child(category.id).child(WORDS_REF)
        wordsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch(dispatcher) {
                    selectedWords.clear()
                    snapshot.children.forEach {
                        it.getValue(Word::class.java)
                            ?.let { it1 -> selectedWords.add(WordX(it1)) }
                    }
                    delay(300)
                    _stateFlow.value = AddYourselfUiState.Success(selectedWords)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _stateFlow.value = AddYourselfUiState.Error(firebaseError(error.code))
            }
        })
    }

    fun checkAllWords() {
        viewModelScope.launch(dispatcher) {
            _isAllSelected.value = !_isAllSelected.value
            selectedWords.forEach { it.isChecked = _isAllSelected.value }
        }
    }

    fun onAddClicked() {
        viewModelScope.launch(dispatcher) {
            _stateFlow.value = AddYourselfUiState.Loading
            insertWords(selectedWords.toList().filter {
                it.isChecked
            })
        }
    }

    private suspend fun insertWords(words: List<WordX>) {
        viewModelScope.launch(dispatcher) {
            firebaseRepository.addWords(category.copy(words = words.map { it.word }))
            _sharedFlow.emit(AddYourselfEventChannel.NavigateBack)
        }
    }
}

sealed class AddYourselfUiState {
    data class Success(val value: List<WordX>) : AddYourselfUiState()
    object Loading : AddYourselfUiState()
    data class Error(@StringRes val msg: Int) : AddYourselfUiState()
}

sealed class AddYourselfEventChannel {
    object NavigateBack : AddYourselfEventChannel()
}