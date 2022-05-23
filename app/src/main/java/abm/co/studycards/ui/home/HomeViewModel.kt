package abm.co.studycards.ui.home

import abm.co.studycards.R
import abm.co.studycards.domain.Prefs
import abm.co.studycards.domain.model.Category
import abm.co.studycards.domain.repository.ServerCloudRepository
import abm.co.studycards.util.base.BaseViewModel
import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prefs: Prefs,
    private val firebaseRepository: ServerCloudRepository
) : BaseViewModel() {

    val dispatcher = Dispatchers.IO

    var defaultCategory: Category? = null

    var fabMenuOpened = false

    var sourceLang = prefs.getSourceLanguage()
        set(value) {
            field = value
            prefs.setSourceLanguage(value)
        }
    var targetLang = prefs.getTargetLanguage()
        set(value) {
            field = value
            prefs.setTargetLanguage(value)
        }

    private val _stateFlow = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val stateFlow = _stateFlow.asStateFlow()

    init {
        viewModelScope.launch(dispatcher) {
            firebaseRepository.fetchUserCategories().collectLatest {
                if(it.isNotEmpty()){
                    defaultCategory = it.first()
                    _stateFlow.value = CategoryUiState.Success(it)
                }else{
                    _stateFlow.value = CategoryUiState.Error(R.string.empty_home_fragment)
                }
            }
        }
    }

    fun removeCategory(category: Category) {
        viewModelScope.launch(dispatcher) {
            firebaseRepository.removeCategory(category)
        }
    }

    fun changePreferenceNativeWithTargetLanguages() {
        targetLang = sourceLang.also {
            sourceLang = targetLang
        }

    }
}

sealed class CategoryUiState {
    data class Success(val value: List<Category>) : CategoryUiState()
    object Loading : CategoryUiState()
    data class Error(@StringRes val msg: Int) : CategoryUiState()
}