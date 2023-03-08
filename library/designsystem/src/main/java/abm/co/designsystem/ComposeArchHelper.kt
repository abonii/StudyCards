package abm.co.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

data class StateDispatch<EVENT, STATE, CHANNEL>(
    val state: STATE,
    val dispatch: (EVENT) -> Unit,
    val channel: Flow<CHANNEL>
)

@Composable
inline fun <reified EVENT, STATE, CHANNEL> use(
    viewModel: UnidirectionalViewModel<EVENT, STATE, CHANNEL>,
): StateDispatch<EVENT, STATE, CHANNEL> {
    val state by viewModel.state.collectAsState()

    val dispatch: (EVENT) -> Unit = { event ->
        viewModel.event(event)
    }

    val channel = viewModel.channel

    return StateDispatch(
        state = state,
        dispatch = dispatch,
        channel = channel
    )
}

interface UnidirectionalViewModel<EVENT, STATE, CHANNEL> {
    val state: StateFlow<STATE>
    val channel: Flow<CHANNEL>
    fun event(event: EVENT)
}

@Suppress("ComposableNaming")
@Composable
fun <T> Flow<T>.collectInLaunchedEffect(function: suspend (value: T) -> Unit) {
    val flow = this
    LaunchedEffect(key1 = flow) {
        flow.collectLatest(function)
    }
}