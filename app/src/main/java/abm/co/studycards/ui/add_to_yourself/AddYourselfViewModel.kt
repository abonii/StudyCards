package abm.co.studycards.ui.add_to_yourself

import abm.co.studycards.data.model.WordX
import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.util.Constants.CATEGORIES_REF
import abm.co.studycards.util.Constants.EXPLORE_REF
import abm.co.studycards.util.Constants.SETS_REF
import abm.co.studycards.util.Constants.WORDS_REF
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AddYourselfViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @Named(CATEGORIES_REF) val categoriesDbRef: DatabaseReference,
    @Named(EXPLORE_REF) val exploreDbRef: DatabaseReference,
    private val repository: ServerCloudRepository,
    prefs: Prefs,
) : BaseViewModel() {

    private val dispatcher = Dispatchers.IO

    private val _stateFlow = MutableStateFlow<AddYourselfUiState>(AddYourselfUiState.Loading)
    val stateFlow = _stateFlow.asStateFlow()

    private val _isAllSelected = MutableStateFlow(true)
    val isAllSelected = _isAllSelected.asStateFlow()

    private val _sharedFlow = MutableSharedFlow<AddYourselfEventChannel>()
    val sharedFlow = _sharedFlow.asSharedFlow()

    var category = savedStateHandle.get<Category>("category")!!

    private val wordsRef = exploreDbRef.child(SETS_REF).child(category.id).child(WORDS_REF)

    val targetLang = prefs.getTargetLanguage()

    private val selectedWords: MutableList<WordX> = ArrayList()

    fun isSelectedAllWords() = selectedWords.count { it.isChecked } == selectedWords.size


    init {
        viewModelScope.launch(dispatcher) {
            wordsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    selectedWords.clear()
                    snapshot.children.forEach {
                        it.getValue(Word::class.java)?.let { it1 -> selectedWords.add(WordX(it1)) }
                    }
                    _stateFlow.value = AddYourselfUiState.Success(selectedWords)
                }

                override fun onCancelled(error: DatabaseError) {
                    _stateFlow.value = AddYourselfUiState.Error(error.message)
                }
            })
        }
    }

    fun checkAllWords(isChecked: Boolean) {
        viewModelScope.launch(dispatcher) {
            selectedWords.forEach { it.isChecked = isChecked }
        }
        _isAllSelected.value = isChecked
    }

    fun onAddClicked() {
        insertWords(selectedWords.toList().filter {
            it.isChecked
        })
    }

    private fun insertWords(words: List<WordX>) {
        viewModelScope.launch(dispatcher) {
            repository.addWords(category.copy(words = words.map { it.word }))
            _sharedFlow.emit(AddYourselfEventChannel.NavigateBack)
        }
    }
}

sealed class AddYourselfUiState {
    data class Success(val value: List<WordX>) : AddYourselfUiState()
    object Loading : AddYourselfUiState()
    data class Error(val msg: String) : AddYourselfUiState()
}

sealed class AddYourselfEventChannel {
    object NavigateBack : AddYourselfEventChannel()
}