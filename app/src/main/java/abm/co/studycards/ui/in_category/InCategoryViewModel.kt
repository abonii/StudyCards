package abm.co.studycards.ui.in_category

import abm.co.studycards.R
import abm.co.studycards.domain.Prefs
import abm.co.studycards.domain.model.Category
import abm.co.studycards.domain.model.ResultWrapper
import abm.co.studycards.domain.model.Word
import abm.co.studycards.domain.usecases.AddUserWordUseCase
import abm.co.studycards.domain.usecases.DeleteUserWordUseCase
import abm.co.studycards.domain.usecases.GetUserCategoryUseCase
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
    private val deleteUserWordUseCase: DeleteUserWordUseCase,
    private val addUserWordUseCase: AddUserWordUseCase,
    private val getUserCategoryUseCase: GetUserCategoryUseCase,
    prefs: Prefs,
) : BaseViewModel() {

    private val dispatcher = Dispatchers.IO

    var category = savedStateHandle.get<Category>("category")!!

    private val _stateFlow =
        MutableStateFlow<InCategoryUiState>(InCategoryUiState.Success(emptyList()))
    val stateFlow = _stateFlow.asStateFlow()

    private val _categoryStateFlow = MutableStateFlow(category)
    val categoryStateFlow = _categoryStateFlow.asStateFlow()

    val targetLang = prefs.getTargetLanguage()
    var isLanguageInstalled = false

    private var databaseRef: DatabaseReference? = null
    private var databaseListener: ValueEventListener? = null

    init {
        viewModelScope.launch(dispatcher) {
            val theCategory = getUserCategoryUseCase(category.id)
            databaseRef = theCategory.second
            databaseListener = theCategory.third
            theCategory.first.collectLatest { wrapper ->
                when (wrapper) {
                    is ResultWrapper.Error -> {
                        _stateFlow.value = InCategoryUiState.Error(wrapper.res)
                    }
                    is ResultWrapper.Success -> {
                        wrapper.value?.let {
                            _categoryStateFlow.value = it
                            if (it.words.isEmpty()) {
                                _stateFlow.value =
                                    InCategoryUiState.Error(R.string.empty_in_category)
                            } else _stateFlow.value = InCategoryUiState.Success(it.words)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun insertWord(word: Word) {
        viewModelScope.launch(dispatcher) {
            addUserWordUseCase(word)
        }
    }

    fun deleteWord(word: Word) {
        viewModelScope.launch(dispatcher) {
            deleteUserWordUseCase(word)
        }
    }

    override fun onCleared() {
        super.onCleared()
        databaseListener?.let { databaseRef?.removeEventListener(it) }
    }

}

sealed class InCategoryUiState {
    data class Success(val category: List<Word>) : InCategoryUiState()
    data class Error(@StringRes val msg: Int) : InCategoryUiState()
}