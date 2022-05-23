package abm.co.studycards.ui.in_category

import abm.co.studycards.R
import abm.co.studycards.domain.Prefs
import abm.co.studycards.domain.model.Category
import abm.co.studycards.domain.model.Word
import abm.co.studycards.domain.repository.ServerCloudRepository
import abm.co.studycards.util.base.BaseViewModel
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InCategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val firebaseRepository: ServerCloudRepository,
    prefs: Prefs,
) : BaseViewModel() {

    private val dispatcher = Dispatchers.IO

    var categoryId = savedStateHandle.get<String>("categoryId")!!

    private val _stateFlow =
        MutableStateFlow<InCategoryUiState>(InCategoryUiState.Loading)
    val stateFlow = _stateFlow.asStateFlow()

    private val _categoryStateFlow =
        MutableStateFlow<Category?>(null)
    val categoryStateFlow = _categoryStateFlow.asStateFlow()

    val targetLang = prefs.getTargetLanguage()
    var isLanguageInstalled = false

    private lateinit var databaseRef: DatabaseReference
    private lateinit var databaseListener: ValueEventListener

    init {
        viewModelScope.launch(dispatcher) {
            val theCategory = firebaseRepository.getTheCategory(categoryId)
            databaseRef = theCategory.second
            databaseListener = theCategory.third
            theCategory.first.collectLatest { category ->
                category?.let {
                    _categoryStateFlow.value = category
                    if (category.words.isNotEmpty())
                        _stateFlow.value = InCategoryUiState.Success(category.words)
                    else _stateFlow.value = InCategoryUiState.Error(R.string.empty)
                }
            }
        }
    }

    fun insertWord(word: Word) {
        viewModelScope.launch(dispatcher) {
            firebaseRepository.addWord(word)
        }
    }

    fun deleteWord(word: Word) {
        viewModelScope.launch(dispatcher) {
            firebaseRepository.removeWord(word)
        }
    }

    override fun onCleared() {
        super.onCleared()
        databaseListener.let { databaseRef.removeEventListener(it) }
    }

}

sealed class InCategoryUiState {
    data class Success(val category: List<Word>) : InCategoryUiState()
    object Loading : InCategoryUiState()
    data class Error(@StringRes val msg: Int) : InCategoryUiState()
}