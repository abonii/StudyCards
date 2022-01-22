package abm.co.studycards.ui.home

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.util.Constants
import abm.co.studycards.util.base.BaseViewModel
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prefs: Prefs,
    @Named(Constants.CATEGORIES_REF) val categoriesDbRef: DatabaseReference,
) : BaseViewModel() {
//
//    private val _stateFlow = MutableStateFlow<CategoryUi>(CategoryUi.Loading)
//    val stateFlow = _stateFlow.asStateFlow()

    var fabMenuOpened = false
    var sourceLang = prefs.getSourceLanguage()
        set(value) {
            field = value
            prefs.setSourceLanguage(value)
        }
    var targetLang = prefs.getTargetLanguage()
        set(value) {
            field = value
            prefs.setTargetLanguage(value)
        }

    fun removeCategory(category: Category) {
        launchIO {
            deleteCategory(category.id)
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