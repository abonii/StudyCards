package abm.co.studycards.ui.select_explore_category

import abm.co.studycards.R
import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.model.vocabulary.CategoryDto
import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.ui.explore.ParentExploreUI
import abm.co.studycards.ui.home.CategoryUiState
import abm.co.studycards.util.Constants
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.firebaseError
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectExploreCategoryViewModel @Inject constructor(
    private val firebaseRepository: ServerCloudRepository
) : BaseViewModel() {

    private val categoriesDbRef = firebaseRepository.getCategoriesReference()

    private val _stateFlow = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val stateFlow = _stateFlow.asStateFlow()

    private var theSetCategories: List<Category> = emptyList()

    init {
        fetchCategories()
    }

    fun fetchTheSetCategory(setId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseRepository.fetchExploreSets().collectLatest { listOfSets ->
                listOfSets.forEach { parentExplore ->
                    if (parentExplore is ParentExploreUI.SetUI && parentExplore.setId == setId) {
                        Log.i(Constants.TAG, "ids are same}")
                        theSetCategories = parentExplore.value.map { it.value }
                        return@collectLatest
                    }
                }
            }
        }
    }

    private fun fetchCategories() {
        categoriesDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch(Dispatchers.IO) {
                    val items = mutableListOf<Category>()
                    snapshot.children.forEach {
                        try {
                            it.getValue(CategoryDto::class.java)
                                ?.let { it1 -> items.add(it1.toCategory()) }
                        } catch (e: DatabaseException) {
                            it.ref.removeValue()
                        }
                    }
                    if (items.size > 0) {
                        _stateFlow.value = CategoryUiState.Success(items.take(500))
                    } else {
                        _stateFlow.value =
                            CategoryUiState.Error(R.string.empty)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _stateFlow.value = CategoryUiState.Error(firebaseError(error.code))
            }
        })
    }

    fun addUserCategoryToExplore(setId: String, category: Category) {
        if (!theSetCategories.contains(category)) {
            firebaseRepository.addExploreCategory(setId, category)
        } else {
            makeToast(R.string.exists)
        }
    }


}