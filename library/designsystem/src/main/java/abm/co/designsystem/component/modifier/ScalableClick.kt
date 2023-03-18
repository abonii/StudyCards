package abm.co.designsystem.component.modifier

import android.view.MotionEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.scalableClick(
    scaleTo: Float = 1.05f,
    onClick: () -> Unit
): Modifier = composed {
    val pressed = remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        animationSpec = tween(),
        targetValue = if (pressed.value) scaleTo else 1f
    )
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }.pointerInteropFilter {
        when (it.action) {
            MotionEvent.ACTION_DOWN -> {
                pressed.value = true
            }
            MotionEvent.ACTION_UP -> {
                pressed.value = false
                onClick()
            }
            MotionEvent.ACTION_CANCEL -> {
                pressed.value = false
            }
        }
        true
    }
}


@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.scalableClick(
    pressed: MutableState<Boolean>,
    onClick: () -> Unit
): Modifier {
    return pointerInteropFilter {
        when (it.action) {
            MotionEvent.ACTION_DOWN -> {
                pressed.value = true
            }
            MotionEvent.ACTION_UP -> {
                pressed.value = false
                onClick()
            }
            MotionEvent.ACTION_CANCEL -> {
                pressed.value = false
            }
        }
        true
    }
}
