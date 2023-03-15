package abm.co.designsystem.component.widget

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.preview.ThemePreviews
import abm.co.designsystem.theme.StudyCardsTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ErrorView(errorMessage: String) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colors.error,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 20.dp)
        )
    }
}

@ThemePreviews
@Composable
private fun ErrorViewPrev() {
    StudyCardsTheme {
        ErrorView(errorMessage = "An unknown error happened!")
    }
}