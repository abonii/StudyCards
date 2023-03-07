package abm.co.studycards.ui.add_to_yourself

import abm.co.studycards.domain.model.Category
import abm.co.studycards.domain.model.ResultWrapper
import abm.co.studycards.domain.model.WordX
import abm.co.studycards.domain.usecases.AddUserCategoryWithIdUseCase
import abm.co.studycards.domain.usecases.GetExploreCategoryUseCase
import abm.co.studycards.util.base.BaseViewModel
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddYourselfViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addUserCategoryWithIdUseCase: AddUserCategoryWithIdUseCase,
    getExploreCategoryUseCase: GetExploreCategoryUseCase
) : BaseViewModel() {

    private val dispatcher = Dispatchers.IO

    private val _stateFlow = MutableStateFlow<AddYourselfUiState>(AddYourselfUiState.Loading)
    val stateFlow = _stateFlow.asStateFlow()

    private val _isAllSelected = MutableStateFlow(true)
    val isAllSelected = _isAllSelected.asStateFlow()

    private val _sharedFlow = MutableSharedFlow<AddYourselfEventChannel>()
    val sharedFlow = _sharedFlow.asSharedFlow()

    private var categoryId = savedStateHandle.get<String>("category_id")!!
    private var setId = savedStateHandle.get<String>("set_id")!!
    private var category: Category? = null

    private var databaseRefListener = ArrayList<Pair<DatabaseReference, ValueEventListener>>()

    private var selectedWords: List<WordX> = ArrayList()

    fun isSelectedAllWords() = selectedWords.count { it.isChecked } == selectedWords.size

    init {
        viewModelScope.launch(dispatcher) {
            delay(400)
            val exploreCategory = getExploreCategoryUseCase(setId, categoryId)
            databaseRefListener.add(exploreCategory.second to exploreCategory.third)
            exploreCategory.first.collectLatest { wrapper ->
                when (wrapper) {
                    is ResultWrapper.Error -> {
                        _stateFlow.value = AddYourselfUiState.Error(wrapper.res)
                    }
                    ResultWrapper.Loading -> {
                        _stateFlow.value = AddYourselfUiState.Loading
                    }
                    is ResultWrapper.Success -> {
                        selectedWords = wrapper.value.words.map { WordX(it) }
                        category = wrapper.value
                        _stateFlow.value = AddYourselfUiState.Success(selectedWords)
                    }
                }
            }


        }
    }

    fun checkAllWords() {
        viewModelScope.launch(dispatcher) {
            _isAllSelected.value = !_isAllSelected.value
            selectedWords.forEach { it.isChecked = _isAllSelected.value }
        }
    }

    fun onAddBtnClicked() {
        viewModelScope.launch(dispatcher) {
            insertCategory(selectedWords.toList().filter { it.isChecked })
        }
    }

    private suspend fun insertCategory(words: List<WordX>) {
        viewModelScope.launch(dispatcher) {
            category?.copy(words = words.map { it.word })
                ?.let { addUserCategoryWithIdUseCase(it) }
            _sharedFlow.emit(AddYourselfEventChannel.NavigateBack)
        }
    }

    override fun onCleared() {
        super.onCleared()
        databaseRefListener.forEach {
            it.first.removeEventListener(it.second)
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