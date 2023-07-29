package abm.co.designsystem.component.about

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.theme.StudyCardsTheme
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

private const val MINIMIZED_LINES = 4

@Composable
fun AboutView(
    details: String,
    readAllText: String,
    collapseText: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = StudyCardsTheme.typography.weight400Size14LineHeight24
        .copy(color = StudyCardsTheme.colors.textPrimary),
) {
    Column(
        modifier = modifier.animateContentSize(),
        horizontalAlignment = Alignment.Start
    ) {
        var isDetailsExpanded by remember { mutableStateOf(false) }
        var isButtonVisible by remember { mutableStateOf(false) }
        Text(
            text = details,
            style = textStyle,
            maxLines = if (isDetailsExpanded) Int.MAX_VALUE else MINIMIZED_LINES,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResult ->
                val lineCount = textLayoutResult.lineCount
                val noOverflow = !textLayoutResult.hasVisualOverflow
                isButtonVisible = !(lineCount <= MINIMIZED_LINES && noOverflow)
            }
        )
        if (isButtonVisible) {
            ExpandCollapseButton(
                isExpanded = isDetailsExpanded,
                collapsedText = readAllText,
                expandedText = collapseText
            ) {
                isDetailsExpanded = !isDetailsExpanded
            }
        }
    }
}
