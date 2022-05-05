package abm.co.studycards.ui.explore

import abm.co.studycards.R
import abm.co.studycards.data.model.vocabulary.Category
import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.util.Constants.CATEGORIES_REF
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.core.App
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    firebaseRepository: ServerCloudRepository,
) : BaseViewModel() {

    val dispatcher = Dispatchers.IO
    private val exploreDbRef = firebaseRepository.getExploreReference()

    private val _stateFlow = MutableStateFlow<ParentExploreUiState>(ParentExploreUiState.Loading)
    val stateFlow = _stateFlow.asStateFlow()

    init {
        exploreDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<ParentExploreUI>()
                val sets1 = mutableListOf<Category>()
                var setName = ""
                viewModelScope.launch(dispatcher) {
                    if (snapshot.child("name").exists())
                        setName = snapshot.child("name").getValue<String>().toString()
                    viewModelScope.launch(dispatcher) {
                        snapshot.child(CATEGORIES_REF).children.forEach {
                            try {
                                it.getValue(Category::class.java)?.let { it1 ->
                                    sets1.add(it1)
                                }
                            } catch (e: DatabaseException) {
                                makeToast(e.message.toString())
                            }
                        }
                    }.join()
                    delay(1500)
                    items.add(
                        ParentExploreUI.SetUI(
                            sets1.map {
                                ChildExploreVHUI.VHCategory(it)
                            }, setName
                        )
                    )
                    if (sets1.isNotEmpty()) {
                        _stateFlow.value = ParentExploreUiState.Success(items)
                    } else {
                        _stateFlow.value =
                            ParentExploreUiState.Error(App.instance.getString(R.string.empty_in_explore))
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                _stateFlow.value = ParentExploreUiState.Error(error.message)
            }

        })
    }

}

sealed class ParentExploreUI {
    data class SetUI(val value: List<ChildExploreVHUI.VHCategory>, val title: String) :
        ParentExploreUI()
}

sealed class ParentExploreUiState {
    data class Success(val value: List<ParentExploreUI>) : ParentExploreUiState()
    data class Error(val error: String) : ParentExploreUiState()
    object Loading : ParentExploreUiState()
}
