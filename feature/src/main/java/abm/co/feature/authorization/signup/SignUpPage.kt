package abm.co.feature.authorization.signup

import abm.co.designsystem.flow.collectInLaunchedEffect
import abm.co.designsystem.component.button.ButtonSize
import abm.co.designsystem.component.button.ButtonState
import abm.co.designsystem.component.button.IconShadowedButton
import abm.co.designsystem.component.button.PrimaryButton
import abm.co.designsystem.component.button.TextButton
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.baseBackground
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.textfield.TextFieldWithLabel
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.designsystem.component.widget.LoadingDialog
import abm.co.feature.R
import abm.co.feature.authorization.common.TrailingIcon
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

@Composable
fun SignUpPage(
    onNavigateLoginPage: () -> Unit,
    onNavigateChooseUserAttributes: () -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        Firebase.analytics.logEvent(
            "sign_up_page_viewed", null
        )
    }
    val state by viewModel.state.collectAsState()
    val startGoogleLoginForResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = viewModel::firebaseAuthWithGoogle
    )
    viewModel.channel.collectInLaunchedEffect {
        when (it) {
            is SignUpContractChannel.LoginViaGoogle -> {
                startGoogleLoginForResult.launch(it.intent)
            }
            SignUpContractChannel.NavigateToChooseUserAttributes -> {
                onNavigateChooseUserAttributes()
            }
            SignUpContractChannel.NavigateToLogin -> {
                onNavigateLoginPage()
            }
            is SignUpContractChannel.ShowMessage -> showMessage(it.messageContent)
        }
    }
    SetStatusBarColor()
    SignUpScreen(
        state = state,
        event = viewModel::event
    )
}


@Composable
private fun SignUpScreen(
    state: SignUpContractState,
    event: (SignUpContractEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .baseBackground()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.11f))
            IntroContent()
            Spacer(modifier = Modifier.weight(0.067f))
            InputFieldsContent(
                email = state.email,
                password = state.password,
                passwordConfirm = state.passwordConfirm,
                onEnterEmailValue = {
                    event(SignUpContractEvent.OnEnterEmailValue(it))
                },
                onEnterPasswordValue = {
                    event(SignUpContractEvent.OnEnterPasswordValue(it))
                },
                onEnterPasswordConfirmValue = {
                    event(SignUpContractEvent.OnEnterPasswordConfirmValue(it))
                }
            )
            Spacer(modifier = Modifier.weight(0.067f))
            PrimaryButton(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                title = stringResource(id = R.string.SignUpPage_SignUpButton),
                components = ButtonSize(),
                buttonState = if (state.isSignUpButtonLoading) ButtonState.Loading else ButtonState.Normal,
                onClick = {
                    event(SignUpContractEvent.OnSignUpViaEmailClicked)
                }
            )
            Spacer(modifier = Modifier.weight(0.044f))
            BottomButtonsContent(
                onClickFacebookButton = {
                    event(SignUpContractEvent.OnLoginViaFacebookClicked)
                },
                onClickGoogleButton = {
                    event(SignUpContractEvent.OnLoginViaGoogleClicked)
                },
                onClickLoginButton = {
                    event(SignUpContractEvent.OnLoginClicked)
                }
            )
            Spacer(modifier = Modifier.weight(0.061f))
        }
        LoadingDialog(
            isVisible = state.isScreenLoading,
            onDismiss = {
                /*Just Ignore*/
            }
        )
    }
}

@Composable
private fun ColumnScope.IntroContent() {
    Text(
        modifier = Modifier.padding(horizontal = 25.dp),
        text = stringResource(id = R.string.SignUpPage_Title),
        style = StudyCardsTheme.typography.weight600Size23LineHeight24.copy(
            color = StudyCardsTheme.colors.textPrimary
        )
    )
    Spacer(modifier = Modifier.weight(0.014f))
    Text(
        text = stringResource(id = R.string.SignUpPage_Subtitle),
        style = StudyCardsTheme.typography.weight400Size20LineHeight20.copy(
            color = StudyCardsTheme.colors.textPrimary
        )
    )
}

@Composable
private fun ColumnScope.InputFieldsContent(
    email: String,
    password: String,
    passwordConfirm: String,
    onEnterEmailValue: (String) -> Unit,
    onEnterPasswordValue: (String) -> Unit,
    onEnterPasswordConfirmValue: (String) -> Unit
) {
    TextFieldWithLabel(
        label = stringResource(id = R.string.SignUpPage_EmailTitle),
        hint = stringResource(id = R.string.SignUpPage_EmailHint),
        value = email,
        onValueChange = onEnterEmailValue,
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .padding(horizontal = 16.dp)
    )
    Spacer(modifier = Modifier.weight(0.024f))
    var showPassword by remember { mutableStateOf(value = false) }
    TextFieldWithLabel(
        label = stringResource(id = R.string.SignUpPage_PasswordTitle),
        hint = stringResource(id = R.string.SignUpPage_PasswordHint),
        value = password,
        onValueChange = onEnterPasswordValue,
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
    Spacer(modifier = Modifier.weight(0.024f))
    TextFieldWithLabel(
        label = stringResource(id = R.string.SignUpPage_PasswordConfirmTitle),
        hint = stringResource(id = R.string.SignUpPage_PasswordConfirmHint),
        value = passwordConfirm,
        onValueChange = onEnterPasswordConfirmValue,
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
}

@Composable
private fun ColumnScope.BottomButtonsContent(
    onClickGoogleButton: () -> Unit,
    onClickFacebookButton: () -> Unit,
    onClickLoginButton: () -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 27.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Divider(
            modifier = Modifier.weight(1f),
            color = StudyCardsTheme.colors.stroke,
            thickness = 1.dp
        )
        Text(
            text = stringResource(id = R.string.SignUpPage_AlternativeSignUp),
            style = StudyCardsTheme.typography.weight400Size14LineHeight20,
            color = StudyCardsTheme.colors.textPrimary
        )
        Divider(
            modifier = Modifier.weight(1f),
            color = StudyCardsTheme.colors.stroke,
            thickness = 1.dp
        )
    }
    Spacer(modifier = Modifier.weight(0.049f))
    Row(
        modifier = Modifier.padding(horizontal = 27.dp),
        horizontalArrangement = Arrangement.spacedBy(17.dp)
    ) {
        IconShadowedButton(
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.ic_google,
            onClick = onClickGoogleButton
        )
        IconShadowedButton(
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.ic_facebook,
            onClick = onClickFacebookButton
        )
    }
    Spacer(modifier = Modifier.weight(0.049f))
    Row(
        modifier = Modifier.padding(horizontal = 27.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.LoginPage_SignUpTitle),
            style = StudyCardsTheme.typography.weight400Size14LineHeight20.copy(
                color = StudyCardsTheme.colors.textPrimary
            )
        )
        TextButton(
            title = stringResource(id = R.string.LoginPage_SignUpNow),
            textStyle = StudyCardsTheme.typography.weight500Size14LineHeight20.copy(
                color = StudyCardsTheme.colors.buttonPrimary
            ),
            onClick = onClickLoginButton
        )
    }
}
