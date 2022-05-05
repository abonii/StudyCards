package abm.co.studycards.ui.add_category

import abm.co.studycards.R
import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.core.App
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditCategoryViewModel @Inject constructor(
    private val repository: ServerCloudRepository,
    private val prefs: Prefs,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    val category = savedStateHandle.get<Category>("category")
    var mainName = MutableStateFlow(category?.mainName ?: "")

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    fun saveCategory() {
        if (category != null) {
            if (category.id.isNotEmpty()) {
                val updatedCategory = category.copy(mainName = mainName.value)
                updateCategory(updatedCategory)
            } else {
                val updatedCategory = category.copy(mainName = App.instance.getString(R.string.remove_this_category))
                updateCategory(updatedCategory)
            }
        } else {
            val newCategory = Category(
                mainName = mainName.value,
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