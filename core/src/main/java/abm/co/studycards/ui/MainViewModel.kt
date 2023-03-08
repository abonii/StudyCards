package abm.co.studycards.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow

@HiltViewModel
class MainViewModel @Inject constructor(

) : ViewModel(), MainContract {

    private val mutableState = MutableStateFlow(
        MainContract.State(
            isLoggedIn = false
        )
    )
    override val state: StateFlow<MainContract.State> = mutableState.asStateFlow()
    override val channel: Flow<Nothing> get() = emptyFlow()

    override fun event(event: MainContract.Event) = when (event) {
        MainContract.Event.OnRefresh -> {

        }
    }
}
