package abm.co.studycards.ui.add_word.dialog.category

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SelectCategoryDialogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    firebaseRepository: ServerCloudRepository
) : BaseViewModel() {

    val categoriesDbRef = firebaseRepository.getCategoriesReference()

    val categoryId = savedStateHandle.get<String>("categoryId")
    var categoryStateFlow = MutableStateFlow<Category?>(null)

}