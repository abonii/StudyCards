package abm.co.studycards.ui

import abm.co.designsystem.base.BaseViewModel
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class MainViewModel @Inject constructor(

):   BaseViewModel(), MainContract {

    private val mutableState = MutableStateFlow(
        MainContract.State(
            isLoggedIn = false
        )
    )
    override val state: StateFlow<MainContract.State> = mutableState.asStateFlow()

    override fun event(event: MainContract.Event) = when (event) {
        MainContract.Event.OnRefresh -> {

        }
    }
}
