package abm.co.feature.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow

@HiltViewModel
class HomeViewModel @Inject constructor(
) : ViewModel(), HomeContract {

    override val channel: Flow<Nothing> get() = emptyFlow()

    private val mutableState = MutableStateFlow(HomeContract.State())
    override val state: StateFlow<HomeContract.State> = mutableState.asStateFlow()

    override fun event(event: HomeContract.Event) = when (event) {
        HomeContract.Event.OnRefresh -> {

        }
    }
}
