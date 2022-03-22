package abm.co.studycards.ui.in_category

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.util.Constants
import abm.co.studycards.util.Constants.WORDS_REF
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class InCategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @Named(Constants.CATEGORIES_REF) val categoriesDbRef: DatabaseReference,
    prefs: Prefs,
) : BaseViewModel() {

    private val dispatcher = Dispatchers.IO

    private val _stateFlow = MutableStateFlow<InCategoryUiState>(InCategoryUiState.Loading)
    val stateFlow = _stateFlow.asStateFlow()

    var category = savedStateHandle.get<Category>("category")!!
    private val _categoryStateFlow = MutableStateFlow(category)
    val categoryStateFlow = _categoryStateFlow.asStateFlow()

    private val thisCategoryRef = categoriesDbRef.child(category.id)
    private val wordsRef = thisCategoryRef.child(WORDS_REF)

    val targetLang = prefs.getTargetLanguage()
    var isLanguageInstalled = false

    init {
        viewModelScope.launch {
            thisCategoryRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(Category::class.java)?.let {
                        _categoryStateFlow.value = it
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _stateFlow.value = InCategoryUiState.Error(error.message)
                }
            })
            wordsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = mutableListOf<Word>()
                    snapshot.children.forEach {
                        it.getValue(Word::class.java)?.let { it1 -> items.add(it1) }
                    }
                    _stateFlow.value = InCategoryUiState.Success(items)
                }

                override fun onCancelled(error: DatabaseError) {
                    _stateFlow.value = InCategoryUiState.Error(error.message)
                }
            })
        }
    }

    fun insertWord(word: Word) {
        viewModelScope.launch(dispatcher) {
            val ref = categoriesDbRef.child(word.categoryID).child(WORDS_REF).push()
            ref.setValue(word.copy(wordId = ref.key ?: "empty-key"))
        }
    }

    fun deleteWord(word: Word) {
        viewModelScope.launch(dispatcher) {
            categoriesDbRef.child(word.categoryID).child(WORDS_REF).child(word.wordId).removeValue()
        }
    }

}

sealed class InCategoryUiState {
    data class Success(val value: List<Word>) : InCategoryUiState()
    object Loading : InCategoryUiState()
    data class Error(val msg: String) : InCategoryUiState()
}