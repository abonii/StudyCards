package abm.co.studycards.ui.add_word.dialog.category

import abm.co.studycards.domain.model.CategorySelectable
import abm.co.studycards.domain.model.ResultWrapper
import abm.co.studycards.domain.usecases.GetUserCategoriesUseCase
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectCategoryDialogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getUserCategoriesUseCase: GetUserCategoriesUseCase
) : BaseViewModel() {

    private val _allCategoryStateFlow = MutableStateFlow<List<CategorySelectable>>(emptyList())
    val allCategoryStateFlow = _allCategoryStateFlow.asStateFlow()

    val categoryId = savedStateHandle.get<String>("categoryId")
    var selectedPos = -1

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getUserCategoriesUseCase().collectLatest {
                if(it is ResultWrapper.Success) {
                    _allCategoryStateFlow.value = it.value.mapIndexed { index, category ->
                        if(category.id == categoryId){
                            selectedPos = index
                        }
                        CategorySelectable(
                            category.copy(words = emptyList()),
                            category.id == categoryId
                        )
                    }
                }
            }
        }
    }

}