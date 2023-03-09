package abm.co.feature.signup

import abm.co.designsystem.collectInLaunchedEffect
import abm.co.designsystem.component.SetStatusBarColor
import abm.co.designsystem.component.button.ButtonSize
import abm.co.designsystem.component.button.ButtonState
import abm.co.designsystem.component.button.IconShadowedButton
import abm.co.designsystem.component.button.PrimaryButton
import abm.co.designsystem.component.button.TextButton
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.baseBackground
import abm.co.designsystem.component.textfield.TextFieldWithLabel
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.designsystem.theme.StudyCardsTypography
import abm.co.designsystem.use
import abm.co.feature.R
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import abm.co.designsystem.R as dR

@Composable
fun SignUpPage(
    onNavigateLoginPage: () -> Unit,
    onNavigateHomePage: () -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val (state, event, channel) = use(viewModel = viewModel)
    val startGoogleLoginForResult = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let {
                viewModel.firebaseAuthWithGoogle(it)
            }
        }
    }
    channel.collectInLaunchedEffect {
        when (it) {
            is SignUpContract.Channel.LoginViaGoogle -> {
                startGoogleLoginForResult.launch(it.intent)
            }
            SignUpContract.Channel.NavigateToHome -> {
                onNavigateHomePage()
            }
            SignUpContract.Channel.NavigateToLogin -> {
                onNavigateLoginPage()
            }
            is SignUpContract.Channel.ShowMessage -> showMessage(it.messageContent)
        }
    }
    SetStatusBarColor()
    SignUpScreen(
        state = state,
        event = event
    )
}


@Composable
private fun SignUpScreen(
    state: SignUpContract.State,
    event: (SignUpContract.Event) -> Unit,
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
                    event(SignUpContract.Event.OnEnterEmailValue(it))
                },
                onEnterPasswordValue = {
                    event(SignUpContract.Event.OnEnterPasswordValue(it))
                },
                onEnterPasswordConfirmValue = {
                    event(SignUpContract.Event.OnEnterPasswordConfirmValue(it))
                }
            )
            Spacer(modifier = Modifier.weight(0.067f))
            PrimaryButton(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                title = stringResource(id = R.string.SignUpPage_SignUpButton),
                components = ButtonSize(),
                buttonState = if (state.isLoginButtonLoading) ButtonState.Loading else ButtonState.Normal,
                onClick = {
                    event(SignUpContract.Event.OnSignUpViaEmailClicked)
                }
            )
            Spacer(modifier = Modifier.weight(0.044f))
            BottomButtonsContent(
                onClickFacebookButton = {
                    event(SignUpContract.Event.OnLoginViaFacebookClicked)
                },
                onClickGoogleButton = {
                    event(SignUpContract.Event.OnLoginViaGoogleClicked)
                },
                onClickLoginButton = {
                    event(SignUpContract.Event.OnLoginClicked)
                }
            )
            Spacer(modifier = Modifier.weight(0.061f))
        }
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
        label = stringResource(id = R.string.SignUpPage_UsernameTitle),
        hint = stringResource(id = R.string.SignUpPage_UsernameHint),
        value = email,
        onValueChange = onEnterEmailValue,
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .padding(horizontal = 16.dp)
    )
    Spacer(modifier = Modifier.weight(0.024f))
    TextFieldWithLabel(
        label = stringResource(id = R.string.SignUpPage_PasswordTitle),
        hint = stringResource(id = R.string.SignUpPage_PasswordHint),
        value = password,
        onValueChange = onEnterPasswordValue,
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
