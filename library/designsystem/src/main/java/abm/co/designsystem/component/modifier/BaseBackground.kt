package abm.co.designsystem.component.modifier

import abm.co.designsystem.theme.StudyCardsTheme
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush

fun Modifier.baseBackground(): Modifier = composed {
    this.background(
        brush = Brush.linearGradient(
            colors = listOf(
                StudyCardsTheme.colors.primary.copy(alpha = .1f),
                StudyCardsTheme.colors.backgroundPrimary,
                StudyCardsTheme.colors.backgroundPrimary,
                StudyCardsTheme.colors.primary.copy(alpha = .05f)
            )
        )
    )
}