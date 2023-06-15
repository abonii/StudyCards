package abm.co.designsystem.extensions

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Suppress("ComposableNaming")
@Composable
fun <T> Flow<T>.collectInLaunchedEffect(
    key: Any = Unit,
    function: suspend (value: T) -> Unit
) {
    LaunchedEffect(key) {
        this@collectInLaunchedEffect.collectLatest(function)
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun <T> Flow<T>.collectInLifecycle(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend (value: T) -> Unit
) {
    val lifecycle = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(
            state = state,
            block = {
                this@collectInLifecycle.collectLatest(block)
            }
        )
    }
}