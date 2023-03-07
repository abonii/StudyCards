package abm.co.feature.login

import abm.co.designsystem.base.BaseViewModel
import abm.co.feature.home.HomeContract
import androidx.compose.ui.graphics.Color
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class LoginViewModel @Inject constructor(
) : BaseViewModel(), LoginContract {

    private val mutableState = MutableStateFlow(
        LoginContract.State(
            color = Color(
                Random.nextInt(255),
                Random.nextInt(255),
                Random.nextInt(255)
            )
        )
    )
    override val state: StateFlow<LoginContract.State> = mutableState.asStateFlow()

    override fun event(event: LoginContract.Event) = when (event) {
        LoginContract.Event.OnRefresh -> {

        }
    }
}
