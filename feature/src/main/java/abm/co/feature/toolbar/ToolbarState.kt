package abm.co.feature.toolbar

import abm.co.feature.toolbar.scrollflags.ExitUntilCollapsedState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Stable
interface ToolbarState {
    val offset: Float
    val height: Float
    val progress: Float
    var scrollValue: Int
}

@Composable
fun rememberToolbarState(
    minHeight: Dp,
    maxHeight: Dp
): ToolbarState {
    val minToolbarHeight = with(LocalDensity.current) {
        minHeight.roundToPx()
    }
    val maxToolbarHeight = with(LocalDensity.current) {
        maxHeight.roundToPx()
    }
    val toolbarHeightRange: IntRange = remember(maxToolbarHeight, minToolbarHeight) {
        minToolbarHeight..maxToolbarHeight
    }
    return rememberSaveable(saver = ExitUntilCollapsedState.Saver) {
        ExitUntilCollapsedState(toolbarHeightRange)
    }
}