package abm.co.studycards.ui.add_category

import abm.co.studycards.R
import abm.co.studycards.domain.Prefs
import abm.co.studycards.domain.model.Category
import abm.co.studycards.domain.usecases.AddUserCategoryUseCase
import abm.co.studycards.domain.usecases.UpdateUserCategoryUseCase
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
    private val addUserCategoryUseCase: AddUserCategoryUseCase,
    private val updateUserCategoryUseCase: UpdateUserCategoryUseCase,
    private val prefs: Prefs,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    val category = savedStateHandle.get<Category>("category")
    var mainName = MutableStateFlow(category?.name ?: "")

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    fun saveCategory() {
        if (category != null) {
            if (category.id.isNotEmpty()) {
                val updatedCategory = category.copy(name = mainName.value)
                updateCategory(updatedCategory)
            } else {
                val updatedCategory =
                    category.copy(name = App.instance.getString(R.string.remove_this_category))
                updateCategory(updatedCategory)
            }
        } else {
            val newCategory = Category(
                name = mainName.value,
                sourceLanguage = prefs.getSourceLanguage(),
                targetLanguage = prefs.getTargetLanguage(),
                id = "default",
                imageUrl = "",
                creatorId = "me",
                creatorName = "ku",
                words = emptyList()
            )
            insertCategory(newCategory)
        }
    }

    private fun insertCategory(category: Category) {
        viewModelScope.launch(dispatcher) {
            addUserCategoryUseCase(category)
        }
    }

    private fun updateCategory(updatedCategory: Category) {
        viewModelScope.launch(dispatcher) {
            updateUserCategoryUseCase(updatedCategory)
        }
    }

}