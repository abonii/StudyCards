package abm.co.studycards.ui.in_explore

import abm.co.studycards.domain.Prefs
import abm.co.studycards.domain.model.Category
import abm.co.studycards.domain.model.ResultWrapper
import abm.co.studycards.domain.model.Word
import abm.co.studycards.domain.usecases.DeleteExploreCategoryUseCase
import abm.co.studycards.domain.usecases.GetCurrentUserUseCase
import abm.co.studycards.domain.usecases.GetExploreCategoryUseCase
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
class InExploreViewModel @Inject constructor(
    private val deleteExploreCategoryUseCase: DeleteExploreCategoryUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    getUserCategoryUseCase: GetUserCategoryUseCase,
    getExploreCategoryUseCase: GetExploreCategoryUseCase,
    savedStateHandle: SavedStateHandle,
    prefs: Prefs,
) : BaseViewModel() {

    private val dispatcher = Dispatchers.IO

    private val _stateFlow = MutableStateFlow<InExploreUiState>(InExploreUiState.Loading)
    val stateFlow = _stateFlow.asStateFlow()

    var category = savedStateHandle.get<Category>("category")!!
    var setId = savedStateHandle.get<String>("set_id")!!

    val iCreatedThisCategory = category.creatorId == getCurrentUserUseCase()?.uid

    val categoryPhotoUrl = category.imageUrl

    val targetLang = prefs.getTargetLanguage()

    val isThisCategoryExistInMine = MutableStateFlow(true)

    private var databaseRefListener = ArrayList<Pair<DatabaseReference, ValueEventListener>>()

    var wordsCount = MutableStateFlow(0)

    init {
        viewModelScope.launch(dispatcher) {
            val userCategory = getUserCategoryUseCase(category.id)
            val exploreCategory = getExploreCategoryUseCase(setId, category.id)

            databaseRefListener.add(userCategory.second to userCategory.third)
            databaseRefListener.add(exploreCategory.second to exploreCategory.third)
            viewModelScope.launch {
                exploreCategory.first.collectLatest {
                    when (it) {
                        is ResultWrapper.Error -> {
                            _stateFlow.value = InExploreUiState.Error(it.res)
                        }
                        ResultWrapper.Loading -> {
                            _stateFlow.value = InExploreUiState.Loading
                        }
                        is ResultWrapper.Success -> {
                            wordsCount.value = it.value.words.size
                            _stateFlow.value = InExploreUiState.Success(it.value.words)
                        }
                    }
                }
            }
            viewModelScope.launch {
                userCategory.first.collectLatest { wrapper ->
                    isThisCategoryExistInMine.value =
                        wrapper is ResultWrapper.Success && wrapper.value != null
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        databaseRefListener.forEach {
            it.first.removeEventListener(it.second)
        }
    }

    fun deleteTheExploreCategory() {
        viewModelScope.launch(dispatcher) {
            deleteExploreCategoryUseCase(setId, category)
        }
    }

}

sealed class InExploreUiState {
    data class Success(val value: List<Word>) : InExploreUiState()
    object Loading : InExploreUiState()
    data class Error(@StringRes val msg: Int) : InExploreUiState()
}