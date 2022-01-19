package abm.co.studycards.ui.add_word.dialog.category

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.util.Constants
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SelectCategoryDialogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @Named(Constants.CATEGORIES_REF) val categoriesDbRef: DatabaseReference,
): BaseViewModel() {

    val category = savedStateHandle.get<Category?>("category")
    var categoryLive: MutableLiveData<Category?> = MutableLiveData(category)

}