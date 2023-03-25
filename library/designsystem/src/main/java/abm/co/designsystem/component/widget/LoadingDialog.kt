package abm.co.designsystem.component.widget

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.theme.StudyCardsTheme
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable

@Composable
fun LoadingDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        LoadingView(
            modifier = Modifier
                .clickableWithoutRipple { /*Ignore*/ }
                .background(StudyCardsTheme.colors.onyx.copy(0.1f))
                .fillMaxSize()
        )
        BackHandler {
            onDismiss()
        }
    }
}