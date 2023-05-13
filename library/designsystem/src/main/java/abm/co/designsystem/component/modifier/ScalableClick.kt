package abm.co.designsystem.component.modifier

import android.view.MotionEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.scalableClick(
    scaleTo: Float = 0.95f,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    longClickDelay: Long = 700
): Modifier = composed {
    val scope = rememberCoroutineScope()
    val longClickJob = remember { mutableStateOf<Job?>(null) }
    val pressed = remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        animationSpec = tween(),
        targetValue = if (pressed.value) scaleTo else 1f
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }.pointerInteropFilter { event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pressed.value = true
                onLongClick?.let {
                    longClickJob.value = scope.launch {
                        delay(longClickDelay)
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                pressed.value = false
                longClickJob.value?.let {
                    if (it.isCompleted) {
                        onLongClick?.invoke()
                    } else {
                        onClick()
                    }
                } ?: onClick()
            }

            MotionEvent.ACTION_CANCEL -> {
                pressed.value = false
                longClickJob.value?.cancel()
                longClickJob.value = null
            }
        }
        true
    }
}


@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.scalableClick(
    pressed: MutableState<Boolean>,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    longClickDelay: Long = 700
): Modifier = composed {
    val scope = rememberCoroutineScope()
    val longClickJob = remember { mutableStateOf<Job?>(null) }

    pointerInteropFilter { event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pressed.value = true
                onLongClick?.let {
                    longClickJob.value = scope.launch {
                        delay(longClickDelay)
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                pressed.value = false
                longClickJob.value?.let {
                    if (it.isCompleted) {
                        onLongClick?.invoke()
                    } else {
                        onClick()
                    }
                } ?: onClick()
            }

            MotionEvent.ACTION_CANCEL -> {
                pressed.value = false
                longClickJob.value?.cancel()
                longClickJob.value = null
            }
        }
        true
    }
}
