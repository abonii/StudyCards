package abm.co.designsystem.message.alert

import abm.co.designsystem.R
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.message.common.MessageAlertContent
import abm.co.designsystem.theme.StudyCardsTheme
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource

@Composable
fun MessageAlertDialog(
    showAlertDialog: MessageAlertContent?,
    onDismiss: () -> Unit,
) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(showAlertDialog) {
        if (showAlertDialog != null) {
            scale.animateTo(1f, animationSpec = spring())
        } else {
            scale.animateTo(0f, animationSpec = spring())
        }
    }

    showAlertDialog?.let {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .graphicsLayer(
                        scaleX = scale.value,
                        scaleY = scale.value
                    )
                    .alpha(scale.value),
                contentAlignment = Alignment.Center
            ) {
                AlertDialog(
                    onDismissRequest = onDismiss,
                    title = { Text(text = showAlertDialog.title) },
                    text = { Text(text = showAlertDialog.subtitle) },
                    confirmButton = {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = StudyCardsTheme.colors.buttonPrimary,
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = stringResource(id = R.string.Messages_OK))
                        }
                    }
                )
            }
        }
    }
}