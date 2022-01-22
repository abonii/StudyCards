package abm.co.studycards.ui.learn.confirmend

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.util.Constants
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ConfirmEndViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @Named(Constants.CATEGORIES_REF) val categoriesDbRef: DatabaseReference,
) : BaseViewModel() {
    val confirmType = savedStateHandle.get<ConfirmText>("confirmType")
    val category = savedStateHandle.get<Category>("category")

    fun deleteThisCategory() {
        viewModelScope.launch {
            if (category != null) {
                deleteCategory(category.id)
            }
        }
    }

    private fun deleteCategory(categoryId: String) {
        launchIO {
            launchIO {
                categoriesDbRef.child(categoryId).removeValue()
            }
        }
    }
}