package abm.co.feature.registration

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow

@HiltViewModel
class RegistrationViewModel @Inject constructor(
) : ViewModel(), RegistrationContract {

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
    override val channel: Flow<Nothing> get() = emptyFlow()

    override fun event(event: RegistrationContract.Event) = when (event) {
        RegistrationContract.Event.OnRefresh -> {

        }
    }
}
