package abm.co.designsystem.component.modifier

import android.os.SystemClock
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

private const val DEFAULT_INTERVAL_TIME = 800

fun Modifier.safeClickable(
    enabled: Boolean = true,
    clickInterval: Int? = null,
    onClick: () -> Unit
) = composed {
    val defaultInterval = remember { clickInterval ?: DEFAULT_INTERVAL_TIME }
    var lastTime by remember { mutableStateOf(0L) }
    clickable(
        enabled = enabled,
        onClick = {
            if (SystemClock.elapsedRealtime() - lastTime >= defaultInterval) {
                lastTime = SystemClock.elapsedRealtime()
                onClick()
            }
        }
    )
}
