package abm.co.designsystem.component.modifier

import android.view.MotionEvent
import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter


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