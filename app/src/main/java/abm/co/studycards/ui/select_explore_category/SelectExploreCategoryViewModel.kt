package abm.co.studycards.ui.select_explore_category

import abm.co.studycards.R
import abm.co.studycards.domain.model.Category
import abm.co.studycards.domain.repository.ServerCloudRepository
import abm.co.studycards.ui.explore.ParentExploreUI
import abm.co.studycards.ui.home.CategoryUiState
import abm.co.studycards.util.Constants
import abm.co.studycards.util.base.BaseViewModel
import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectExploreCategoryViewModel @Inject constructor(
    private val firebaseRepository: ServerCloudRepository
) : BaseViewModel() {

    private val _stateFlow = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val stateFlow = _stateFlow.asStateFlow()

    private var theSetCategories: List<Category> = emptyList()

    init {
        fetchCategories()
    }

    fun fetchTheSetCategory(setId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseRepository.fetchExploreSets().collectLatest { listOfSets ->
                listOfSets.forEach { parentExplore ->
                    if (parentExplore is ParentExploreUI.SetUI && parentExplore.setId == setId) {
                        Log.i(Constants.TAG, "ids are same}")
                        theSetCategories = parentExplore.value.map { it.value }
                        return@collectLatest
                    }
                }
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseRepository.fetchUserCategories().collectLatest {
                if(it.isNotEmpty()){
                    _stateFlow.value = CategoryUiState.Success(it)
                }else{
                    _stateFlow.value = CategoryUiState.Error(R.string.empty_home_fragment)
                }
            }
        }
    }

    fun addUserCategoryToExplore(setId: String, category: Category) {
        viewModelScope.launch {
            if (!theSetCategories.contains(category)) {
                firebaseRepository.addExploreCategory(setId, category)
            } else {
                makeToast(R.string.exists)
            }
        }
    }

}