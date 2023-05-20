package abm.co.designsystem.component.dialog

import abm.co.designsystem.R
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.theme.StudyCardsTheme
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun ShowDialogOnBackPressed(
    onConfirm: () -> Unit,
    subtitle: String? = null,
    show: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    title: String = stringResource(id = R.string.Alert_Title),
    confirm: String = stringResource(id = R.string.Alert_Confirm),
    dismiss: String = stringResource(id = R.string.Alert_Dismiss)
) {
    val showState = remember(show) { mutableStateOf(show) }
    BackHandler {
        showState.value = true
    }
    if (showState.value) {
        ConfirmAlertDialog(
            title = title,
            subtitle = subtitle,
            confirm = confirm,
            dismiss = dismiss,
            onConfirm = onConfirm,
            onDismiss = {
                onDismiss?.invoke()
                showState.value = false
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
    @DrawableRes imageRes: Int? = null,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                imageRes?.let {
                    Image(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .align(CenterHorizontally),
                        painter = painterResource(id = imageRes),
                        contentDescription = null
                    )
                }
                Text(
                    modifier = Modifier.padding(bottom = 10.dp)
                            then if (imageRes != null)
                        Modifier.align(CenterHorizontally) else Modifier,
                    text = title,
                    style = StudyCardsTheme.typography.weight500Size14LineHeight20
                )
            }
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