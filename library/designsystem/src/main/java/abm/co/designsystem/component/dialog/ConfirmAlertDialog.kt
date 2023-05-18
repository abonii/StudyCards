package abm.co.designsystem.component.dialog

import abm.co.designsystem.R
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.theme.StudyCardsTheme
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun ShowDialogOnBackPressed(
    onConfirm: () -> Unit,
    subtitle: String? = null,
    show: MutableState<Boolean> = remember { mutableStateOf(false) },
    title: String = stringResource(id = R.string.Alert_Title),
    confirm: String = stringResource(id = R.string.Alert_Confirm),
    dismiss: String = stringResource(id = R.string.Alert_Dismiss)
) {
    BackHandler {
        show.value = true
    }
    if (show.value) {
        ConfirmAlertDialog(
            title = title,
            subtitle = subtitle,
            confirm = confirm,
            dismiss = dismiss,
            onConfirm = onConfirm,
            onDismiss = {
                show.value = false
            }
        )
    }
}

@Composable
fun ConfirmAlertDialog(
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirm: String = stringResource(id = R.string.Alert_Confirm),
    dismiss: String = stringResource(id = R.string.Alert_Dismiss),
    subtitle: String? = null,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                modifier = Modifier.padding(bottom = 10.dp),
                text = title,
                style = StudyCardsTheme.typography.weight500Size14LineHeight20
            )
        },
        text = subtitle?.let {
            {
                Text(
                    text = subtitle,
                    style = StudyCardsTheme.typography.weight400Size14LineHeight20
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = StudyCardsTheme.colors.error,
                    contentColor = Color.White
                )
            ) {
                Text(text = confirm)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = StudyCardsTheme.colors.buttonPrimary,
                    contentColor = Color.White
                )
            ) {
                Text(text = dismiss)
            }
        },
        backgroundColor = StudyCardsTheme.colors.backgroundPrimary,
        contentColor = StudyCardsTheme.colors.textPrimary
    )
}