package abm.co.studycards.ui.add_word.dialog.category

import abm.co.studycards.domain.model.CategorySelectable
import abm.co.studycards.domain.repository.ServerCloudRepository
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
    firebaseRepository: ServerCloudRepository
) : BaseViewModel() {

    private val _allCategoryStateFlow = MutableStateFlow<List<CategorySelectable>>(emptyList())
    val allCategoryStateFlow = _allCategoryStateFlow.asStateFlow()

    val categoryId = savedStateHandle.get<String>("categoryId")

    init {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseRepository.fetchUserCategories().collectLatest {
                _allCategoryStateFlow.value = it.map { category ->
                    CategorySelectable(
                        category.copy(words = emptyList()),
                        category.id == categoryId
                    )
                }
            }
        }
    }

}