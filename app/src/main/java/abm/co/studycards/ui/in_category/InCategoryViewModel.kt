package abm.co.studycards.ui.in_category

import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.model.vocabulary.Word
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.util.Constants.WORDS_REF
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.core.App
import abm.co.studycards.util.firebaseError
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InCategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    firebaseRepository: ServerCloudRepository,
    prefs: Prefs,
) : BaseViewModel() {

    private val dispatcher = Dispatchers.IO
    private val categoriesDbRef = firebaseRepository.getCategoriesReference()

    var categoryId = savedStateHandle.get<String>("categoryId")!!
    var categoryName: String = ""
    private val _categoryStateFlow = MutableStateFlow<Category?>(null)
    val categoryStateFlow = _categoryStateFlow.asStateFlow()

    private val _stateFlow =
        MutableStateFlow<InCategoryUiState>(InCategoryUiState.Loading)
    val stateFlow = _stateFlow.asStateFlow()

    private val thisCategoryRef = categoriesDbRef.child(categoryId)
    private val wordsRef = thisCategoryRef.child(WORDS_REF)

    val targetLang = prefs.getTargetLanguage()
    var isLanguageInstalled = false

    init {
        viewModelScope.launch {
            wordsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    viewModelScope.launch(dispatcher) {
                        val items = mutableListOf<Word>()
                        snapshot.children.forEach {
                            it.getValue(Word::class.java)?.let { it1 -> items.add(it1) }
                        }
                        delay(400)
                        _stateFlow.value = InCategoryUiState.Success(items)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _stateFlow.value = InCategoryUiState.Error(firebaseError(error.code))
                }
            })
            thisCategoryRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(Category::class.java)?.let {
                        _categoryStateFlow.value = it
                        categoryName = it.mainName
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _stateFlow.value = InCategoryUiState.Error(firebaseError(error.code))
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
    data class Error(@StringRes val msg: Int) : InCategoryUiState()
}