package abm.co.feature.home

import abm.co.designsystem.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class HomeViewModel @Inject constructor(
) : BaseViewModel(), HomeContract {

    private val mutableState = MutableStateFlow(
        HomeContract.State(
            color = androidx.compose.ui.graphics.Color(
                kotlin.random.Random.nextInt(255),
                kotlin.random.Random.nextInt(255),
                kotlin.random.Random.nextInt(255)
            )
        )
    )
    override val state: StateFlow<HomeContract.State> = mutableState.asStateFlow()

    override fun event(event: HomeContract.Event) = when (event) {
        HomeContract.Event.OnRefresh -> {

        }
    }
}
