package abm.co.designsystem.component.modifier

import abm.co.designsystem.BuildConfig
import androidx.compose.ui.Modifier

val Modifier = Modifier.run {
    if (BuildConfig.DEBUG) recomposeHighlighter() else this
}