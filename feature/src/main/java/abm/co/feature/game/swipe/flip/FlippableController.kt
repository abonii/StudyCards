package abm.co.feature.game.swipe.flip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * A [FlippableController] which lets you control flipping programmatically.
 *
 * @author Wajahat Karim (https://wajahatkarim.com)
 */
@Immutable
class FlippableController {

    private val _flipRequests = MutableSharedFlow<FlippableState>(extraBufferCapacity = 1)
    internal val flipRequests = _flipRequests.asSharedFlow()

    var currentSide: FlippableState by mutableStateOf(FlippableState.FRONT)
        private set

    /**
     * Flips the view to the [FlippableState.FRONT] side
     */
    private fun flipToFront() {
        flip(FlippableState.FRONT)
    }

    /**
     * Flips the view to the [FlippableState.BACK] side
     */
    private fun flipToBack() {
        flip(FlippableState.BACK)
    }

    /**
     * Flips the view to the passed [flippableState]
     * @param flippableState The side to flip the view to.
     */
    private fun flip(flippableState: FlippableState) {
        currentSide = flippableState
        _flipRequests.tryEmit(flippableState)
    }

    /**
     * Flips the view to the other side. If it's [FlippableState.FRONT] it goes to [FlippableState.BACK] and vice-versa
     */
    fun flip() {
        if (currentSide != FlippableState.BACK)
            flipToBack()
        else flipToFront()
    }

    fun reset() {
        currentSide = FlippableState.FRONT
    }
}

/**
 * Creates an instance of [FlippableController] and remembers it for recomposition.
 */
@Composable
fun rememberFlipController(): FlippableController {
    return remember {
        FlippableController()
    }
}