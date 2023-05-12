package abm.co.feature.profile.changepassword

import abm.co.designsystem.component.button.PrimaryButton
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.baseBackground
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.textfield.TextFieldWithLabel
import abm.co.designsystem.extensions.collectInLaunchedEffect
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.authorization.common.TrailingIcon
import abm.co.feature.utils.AnalyticsManager
import abm.co.feature.utils.StudyCardsConstants
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ChangePasswordPage(
    navigateBack: () -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        AnalyticsManager.sendEvent(
            name = "change_password_page_viewed"
        )
    }

    viewModel.channel.collectInLaunchedEffect(
        function = { contract ->
            when(contract) {
                ChangePasswordContractChannel.NavigateBack -> {
                    navigateBack()
                }
                is ChangePasswordContractChannel.ShowMessage -> showMessage(contract.messageContent)
            }
        }
    )

    val state by viewModel.state.collectAsState()
    SetStatusBarColor()
    Screen(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun Screen(
    state: ChangePasswordContractState,
    onEvent: (ChangePasswordContractEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .baseBackground()
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Toolbar(
            onBack = {
                onEvent(ChangePasswordContractEvent.OnBackClicked)
            }
        )
        var showPassword by remember { mutableStateOf(value = false) }
        TextFieldWithLabel(
            label = stringResource(id = R.string.ChangePassword_CurrentPasswordField_title),
            hint = stringResource(id = R.string.ChangePassword_CurrentPasswordField_hint),
            value = state.currentPassword,
            onValueChange = {
                onEvent(ChangePasswordContractEvent.OnEnterCurrentPassword(it))
            },
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                TrailingIcon(
                    showPassword = showPassword,
                    onClick = {
                        showPassword = it
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextFieldWithLabel(
            label = stringResource(id = R.string.ChangePassword_NewPasswordField_title),
            hint = stringResource(id = R.string.ChangePassword_NewPasswordField_hint),
            value = state.newPassword,
            onValueChange = {
                onEvent(ChangePasswordContractEvent.OnEnterNewPassword(it))
            },
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                TrailingIcon(
                    showPassword = showPassword,
                    onClick = {
                        showPassword = it
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        PrimaryButton(
            title = stringResource(id = R.string.ChangePassword_PrimaryButton_title),
            onClick = {
                onEvent(ChangePasswordContractEvent.OnClickPrimaryButton)
            },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            buttonState = state.primaryButtonState
        )
    }
}

@Composable
private fun Toolbar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(StudyCardsConstants.TOOLBAR_HEIGHT)
            .fillMaxWidth()
            .padding(start = 6.dp, top = 6.dp, bottom = 6.dp, end = 16.dp)
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickableWithoutRipple(onBack)
                .padding(10.dp)
                .size(24.dp),
            painter = painterResource(id = R.drawable.ic_left),
            tint = StudyCardsTheme.colors.opposition,
            contentDescription = null
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(id = R.string.ChangePassword_Toolbar_title),
            style = StudyCardsTheme.typography.weight600Size14LineHeight18,
            color = StudyCardsTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
