package abm.co.studycards.ui.add_category

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.data.repository.VocabularyRepository
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditCategoryViewModel @Inject constructor(
    private val repository: VocabularyRepository,
    private val prefs: Prefs,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val category = savedStateHandle.get<Category>("category")

    var mainName = category?.mainName ?: ""

    fun onSaveCategory() {
        if (category != null) {
            val updatedCategory = category.copy(mainName = mainName)
            updateCategory(updatedCategory)
        } else {
            val newCategory =
                Category(
                    mainName = mainName,
                    sourceLanguage = prefs.getSourceLanguage(),
                    targetLanguage = prefs.getTargetLanguage()
                )
            insertCategory(newCategory)
        }
    }

    private fun insertCategory(category: Category) {
        viewModelScope.launch {
            repository.addCategory(category)
        }
    }

    private fun updateCategory(updatedCategory: Category) {
        viewModelScope.launch {
//            repository.updateCategory(updatedCategory)
        }
    }
}