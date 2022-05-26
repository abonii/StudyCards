package abm.co.studycards.ui.select_explore_category

import abm.co.studycards.R
import abm.co.studycards.domain.model.Category
import abm.co.studycards.domain.model.ResultWrapper
import abm.co.studycards.domain.usecases.AddExploreCategoryUseCase
import abm.co.studycards.domain.usecases.GetExploreSetsUseCase
import abm.co.studycards.domain.usecases.GetUserCategoriesUseCase
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectExploreCategoryViewModel @Inject constructor(
    getUserCategoriesUseCase: GetUserCategoriesUseCase,
    private val getExploreSetsUseCase: GetExploreSetsUseCase,
    private val addExploreCategoryUseCase: AddExploreCategoryUseCase
) : BaseViewModel() {

    val stateFlow = getUserCategoriesUseCase()

    private var userCategoriesId: List<String> = emptyList()

    fun fetchTheSetCategory(setId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getExploreSetsUseCase().collectLatest { listOfSets ->
                if (listOfSets is ResultWrapper.Success) {
                    listOfSets.value.forEach { parentExplore ->
                        if (parentExplore.id == setId) {
                            userCategoriesId = parentExplore.categories.map { it.id }
                            return@forEach
                        }
                    }
                }
            }
        }
    }

    fun addUserCategoryToExplore(setId: String, category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            if (userCategoriesId.any { it == category.id }) {
                makeToast(R.string.exists)
            } else {
                addExploreCategoryUseCase(setId, category)
            }
        }
    }

}