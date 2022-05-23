package abm.co.studycards.ui.add_to_yourself

import abm.co.studycards.R
import abm.co.studycards.domain.model.Category
import abm.co.studycards.domain.model.WordX
import abm.co.studycards.domain.repository.ServerCloudRepository
import abm.co.studycards.util.base.BaseViewModel
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddYourselfViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val firebaseRepository: ServerCloudRepository,
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

    private var selectedWords: List<WordX> = ArrayList()

    fun isSelectedAllWords() = selectedWords.count { it.isChecked } == selectedWords.size

    init {
        viewModelScope.launch(Dispatchers.Default) {
            val exploreCategory = firebaseRepository.getExploreCategory(setId, categoryId)
            exploreCategory.first.collectLatest { category1 ->
                category1?.let { category2 ->
                    this@AddYourselfViewModel.category = category2
                    selectedWords = category2.words.map { WordX(it) }
                    delay(400)
                    if (category2.words.isEmpty())
                        _stateFlow.value = AddYourselfUiState.Error(R.string.empty)
                    else _stateFlow.value = AddYourselfUiState.Success(selectedWords)
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
            _stateFlow.value = AddYourselfUiState.Loading
            insertCategory(selectedWords.toList().filter { it.isChecked })
        }
    }

    private suspend fun insertCategory(words: List<WordX>) {
        viewModelScope.launch(dispatcher) {
            category?.copy(words = words.map { it.word })
                ?.let { firebaseRepository.addWithIdCategory(it) }
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