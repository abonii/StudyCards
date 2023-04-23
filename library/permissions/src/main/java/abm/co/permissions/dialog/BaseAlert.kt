package abm.co.permissions.dialog

import abm.co.designsystem.component.modifier.clickableWithRipple
import abm.co.designsystem.theme.StudyCardsTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BaseAlert(
    title: String,
    onNegativeClick: () -> Unit,
    onPositiveClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    negativeButtonText: String? = null,
    positiveButtonText: String? = null,
    negativeButtonColor: Color = StudyCardsTheme.colors.buttonPrimary,
    positiveButtonColor: Color = StudyCardsTheme.colors.error,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        AlertBody(
            modifier = modifier,
            title = title,
            subtitle = subtitle,
            negativeButtonText = negativeButtonText,
            positiveButtonText = positiveButtonText,
            negativeButtonColor = negativeButtonColor,
            positiveButtonColor = positiveButtonColor,
            onNegativeClick = onNegativeClick,
            onPositiveClick = onPositiveClick,
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun AlertBody(
    title: String,
    subtitle: String?,
    negativeButtonColor: Color,
    positiveButtonColor: Color,
    onNegativeClick: () -> Unit,
    onPositiveClick: () -> Unit,
    modifier: Modifier = Modifier,
    negativeButtonText: String? = null,
    positiveButtonText: String? = null,
    cornerRadius: Dp = 12.dp,
    onDismiss: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickableWithRipple(onClick = onDismiss)
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 40.dp, vertical = 24.dp),
            shape = RoundedCornerShape(cornerRadius),
            backgroundColor = StudyCardsTheme.colors.backgroundSecondary,
            elevation = 0.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, top = 24.dp, end = 24.dp),
                    style = StudyCardsTheme.typography.weight400Size12LineHeight20,
                    color = StudyCardsTheme.colors.textPrimary
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = subtitle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp),
                        style = StudyCardsTheme.typography.weight400Size14LineHeight18,
                        color = StudyCardsTheme.colors.textPrimary
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
                if (positiveButtonText != null) {
                    BaseAlertButton(
                        text = positiveButtonText,
                        textColor = positiveButtonColor,
                        textStyle = StudyCardsTheme.typography.weight600Size14LineHeight18,
                        onClick = onPositiveClick
                    )
                }
                if (negativeButtonText != null) {
                    BaseAlertButton(
                        text = negativeButtonText,
                        textColor = negativeButtonColor,
                        textStyle = StudyCardsTheme.typography.weight400Size14LineHeight18,
                        onClick = onNegativeClick
                    )
                }
            }
        }
    }
}

@Composable
private fun BaseAlertButton(
    text: String,
    textColor: Color,
    textStyle: TextStyle,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickableWithRipple(
                pressedColor = StudyCardsTheme.colors.pressed,
                onClick = onClick
            )
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider(
            color = StudyCardsTheme.colors.stroke,
            thickness = 0.5.dp
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            text = text,
            color = textColor,
            style = textStyle
        )
    }
}
