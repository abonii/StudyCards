package abm.co.designsystem.component.text

import androidx.annotation.PluralsRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.pluralStringResource

@ReadOnlyComposable
@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun pluralString(@PluralsRes id: Int, count: Int): String {
    return pluralStringResource(id, count, count)
}