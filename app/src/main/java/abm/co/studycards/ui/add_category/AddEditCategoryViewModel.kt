package abm.co.studycards.ui.add_category

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditCategoryViewModel @Inject constructor(
    private val repository: ServerCloudRepository,
    private val prefs: Prefs,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    val category = savedStateHandle.get<Category>("category")
    var mainName = category?.mainName ?: ""

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    fun saveCategory() {
        if (category != null) {
            val updatedCategory = category.copy(mainName = mainName)
            updateCategory(updatedCategory)
        } else {
            val newCategory = Category(
                mainName = mainName,
                sourceLanguage = prefs.getSourceLanguage(),
                targetLanguage = prefs.getTargetLanguage()
            )
            insertCategory(newCategory)
        }
    }

    private fun insertCategory(category: Category) {
        viewModelScope.launch(dispatcher) {
            repository.addCategory(category)
        }
    }

    private fun updateCategory(updatedCategory: Category) {
        viewModelScope.launch(dispatcher) {
            repository.updateCategoryName(updatedCategory)
        }
    }

}