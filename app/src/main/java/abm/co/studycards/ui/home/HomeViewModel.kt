package abm.co.studycards.ui.home

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.util.Constants
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prefs: Prefs,
    @Named(Constants.CATEGORIES_REF) val categoriesDbRef: DatabaseReference,
) : BaseViewModel() {

    val dispatcher = Dispatchers.IO

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


    private val _stateFlow = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val stateFlow = _stateFlow.asStateFlow()

    init {
        categoriesDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch(dispatcher) {
                    val items = mutableListOf<Category>()
                    snapshot.children.forEach {
                        it.getValue(Category::class.java)?.let { it1 -> items.add(it1) }
                    }
                    delay(1000)
                    _stateFlow.value = CategoryUiState.Success(items)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _stateFlow.value = CategoryUiState.Error(error.message)
            }
        })
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

sealed class CategoryUiState {
    data class Success(val value: List<Category>) : CategoryUiState()
    object Loading : CategoryUiState()
    data class Error(val msg: String) : CategoryUiState()
}