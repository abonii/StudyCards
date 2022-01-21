package abm.co.studycards.ui.add_category

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.data.repository.VocabularyRepository
import abm.co.studycards.util.Constants
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AddEditCategoryViewModel @Inject constructor(
    private val prefs: Prefs,
    savedStateHandle: SavedStateHandle,
    @Named(Constants.CATEGORIES_REF) private val categoriesDbRef: DatabaseReference,
) : BaseViewModel() {
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
        launchIO {
            addCategory(category)
        }
    }

    private fun addCategory(category: Category) {
        launchIO {
            val ref = categoriesDbRef.push()
            ref.setValue(category.copy(id = ref.key ?: ""))
        }
    }

    private fun updateCategory(updatedCategory: Category) {
        launchIO {
            updateCategoryName(updatedCategory)
        }
    }

    private fun updateCategoryName(category: Category) {
        launchIO {
            categoriesDbRef.child(category.id)
                .updateChildren(mapOf("mainName" to category.mainName))
        }
    }
}