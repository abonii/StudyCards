package abm.co.studycards.ui.explore

import abm.co.studycards.domain.model.ResultWrapper
import abm.co.studycards.domain.usecases.GetExploreSetsUseCase
import abm.co.studycards.util.base.BaseViewModel
import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    getExploreSetsUseCase: GetExploreSetsUseCase
) : BaseViewModel() {

    val dispatcher = Dispatchers.IO

    private val _stateFlow = MutableStateFlow<ParentExploreUiState>(ParentExploreUiState.Loading)
    val stateFlow = _stateFlow.asStateFlow()

    init {
        viewModelScope.launch(dispatcher) {
            delay(800)
            getExploreSetsUseCase().collectLatest { wrapper ->
                when (wrapper) {
                    is ResultWrapper.Error -> {
                        _stateFlow.value = ParentExploreUiState.Error(wrapper.res)
                    }
                    is ResultWrapper.Success -> {
                        val sets = wrapper.value.map {
                            ParentExploreUI.SetUI(it.categories.map { category ->
                                ChildExploreVHUI.VHCategory(category)
                            }, title = it.name, setId = it.id)
                        }
                        _stateFlow.value = ParentExploreUiState.Success(sets)
                    }
                    else -> {
                        _stateFlow.value = ParentExploreUiState.Loading
                    }
                }
            }
        }
    }
}

sealed class ParentExploreUI {
    data class SetUI(
        val value: List<ChildExploreVHUI.VHCategory>,
        val title: String,
        val setId: String
    ) :
        ParentExploreUI()
}

sealed class ParentExploreUiState {
    data class Success(val value: List<ParentExploreUI>) : ParentExploreUiState()
    data class Error(@StringRes val error: Int) : ParentExploreUiState()
    object Loading : ParentExploreUiState()
}
