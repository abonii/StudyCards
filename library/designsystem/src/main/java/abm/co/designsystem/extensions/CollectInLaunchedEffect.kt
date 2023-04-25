package abm.co.designsystem.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Suppress("ComposableNaming")
@Composable
fun <T> Flow<T>.collectInLaunchedEffect(function: suspend (value: T) -> Unit) {
    LaunchedEffect(Unit) {
        this@collectInLaunchedEffect.collectLatest(function)
    }
}