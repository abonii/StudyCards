package abm.co.studycards.ui.in_explore

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
class InExploreViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    firebaseRepository: ServerCloudRepository,
    prefs: Prefs
) : BaseViewModel() {

    private val dispatcher = Dispatchers.IO

    private val _stateFlow = MutableStateFlow<InExploreUiState>(InExploreUiState.Loading)
    val stateFlow = _stateFlow.asStateFlow()

    var category = savedStateHandle.get<Category>("category")!!
    var setId = savedStateHandle.get<String>("set_id")!!

    val categoryPhotoUrl = category.imageUrl

    val targetLang = prefs.getTargetLanguage()

    val isThisCategoryExistInMine = MutableStateFlow(true)

    private var databaseRefListener = ArrayList<Pair<DatabaseReference, ValueEventListener>>()

    var wordsCount = MutableStateFlow(0)

    init {
        viewModelScope.launch(dispatcher) {
            val userCategory = firebaseRepository.getTheCategory(category.id)
            val exploreCategory = firebaseRepository.getExploreCategory(setId, category.id)

            databaseRefListener.add(userCategory.second to userCategory.third)
            databaseRefListener.add(exploreCategory.second to exploreCategory.third)

            viewModelScope.launch {
                exploreCategory.first.collectLatest {
                    it?.let {
                        wordsCount.value = it.words.size
                        _stateFlow.value = InExploreUiState.Success(it.words)
                    }
                }
            }
            viewModelScope.launch {
                userCategory.first.collectLatest { category ->
                    isThisCategoryExistInMine.value = category != null
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

}

sealed class InExploreUiState {
    data class Success(val value: List<Word>) : InExploreUiState()
    object Loading : InExploreUiState()
    data class Error(@StringRes val msg: Int) : InExploreUiState()
}