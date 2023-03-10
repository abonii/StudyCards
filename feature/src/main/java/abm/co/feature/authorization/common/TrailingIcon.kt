package abm.co.feature.authorization.common

import abm.co.designsystem.theme.StudyCardsTheme
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable

@Composable
fun TrailingIcon(showPassword: Boolean, onClick: (Boolean) -> Unit) {
    if (showPassword) {
        IconButton(onClick = { onClick(false) }) {
            Icon(
                imageVector = Icons.Filled.Visibility,
                tint = StudyCardsTheme.colors.onyx,
                contentDescription = null
            )
        }
    } else {
        IconButton(
            onClick = { onClick(true) }) {
            Icon(
                imageVector = Icons.Filled.VisibilityOff,
                tint = StudyCardsTheme.colors.onyx,
                contentDescription = null
            )
        }
    }
}