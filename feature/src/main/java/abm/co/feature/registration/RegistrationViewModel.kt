package abm.co.feature.registration

import abm.co.designsystem.base.BaseViewModel
import abm.co.feature.registration.RegistrationContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class RegistrationViewModel @Inject constructor(
) : BaseViewModel(), RegistrationContract {

    private val mutableState = MutableStateFlow(
        RegistrationContract.State(
            color = androidx.compose.ui.graphics.Color(
                kotlin.random.Random.nextInt(255),
                kotlin.random.Random.nextInt(255),
                kotlin.random.Random.nextInt(255)
            )
        )
    )
    override val state: StateFlow<RegistrationContract.State> = mutableState.asStateFlow()

    override fun event(event: RegistrationContract.Event) = when (event) {
        RegistrationContract.Event.OnRefresh -> {

        }
    }
}
