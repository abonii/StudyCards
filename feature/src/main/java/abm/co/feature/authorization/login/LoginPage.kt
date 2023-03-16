package abm.co.feature.authorization.login

import abm.co.designsystem.component.button.ButtonSize
import abm.co.designsystem.component.button.ButtonState
import abm.co.designsystem.component.button.IconShadowedButton
import abm.co.designsystem.component.button.PrimaryButton
import abm.co.designsystem.component.button.TextButton
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.baseBackground
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.textfield.TextFieldWithLabel
import abm.co.designsystem.component.widget.LoadingDialog
import abm.co.designsystem.flow.collectInLaunchedEffect
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.authorization.common.TrailingIcon
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
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
fun LoginPage(
    onNavigateSignUpPage: () -> Unit,
    onNavigateHomePage: () -> Unit,
    onNavigateChooseUserAttributes: () -> Unit,
    onNavigateToForgotPage: () -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        Firebase.analytics.logEvent(
            "login_page_viewed", null
        )
    }
    val startGoogleLoginForResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = viewModel::firebaseAuthWithGoogle
    )
    val state by viewModel.state.collectAsState()
    viewModel.channel.collectInLaunchedEffect {
        when (it) {
            is LoginContractChannel.LoginViaGoogle -> {
                startGoogleLoginForResult.launch(it.intent)
            }
            LoginContractChannel.NavigateToForgotPassword -> {
                onNavigateToForgotPage()
            }
            LoginContractChannel.NavigateToHome -> {
                onNavigateHomePage()
            }
            LoginContractChannel.NavigateToChooseUserAttributes -> {
                onNavigateChooseUserAttributes()
            }
            LoginContractChannel.NavigateToSignUp -> {
                onNavigateSignUpPage()
            }
            is LoginContractChannel.ShowMessage -> showMessage(it.messageContent)
        }
    }
    SetStatusBarColor()
    LoginScreen(
        state = state,
        event = viewModel::event
    )
}

@Composable
private fun LoginScreen(
    state: LoginContractState,
    event: (LoginContractEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .navigationBarsPadding()
            .imePadding()
            .baseBackground(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.189f))
        IntroContent()
        Spacer(modifier = Modifier.weight(0.067f))
        InputFieldsContent(
            email = state.email,
            password = state.password,
            onEnterEmailValue = {
                event(LoginContractEvent.OnEnterEmailValue(it))
            },
            onEnterPasswordValue = {
                event(LoginContractEvent.OnEnterPasswordValue(it))
            }
        )
        Spacer(modifier = Modifier.weight(0.067f))
        PrimaryButton(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            title = stringResource(id = R.string.LoginPage_LoginButton),
            components = ButtonSize(),
            buttonState = if (state.isLoginButtonLoading) ButtonState.Loading else ButtonState.Normal,
            onClick = {
                event(LoginContractEvent.OnLoginViaEmailClicked)
            }
        )
        Spacer(modifier = Modifier.weight(0.081f))
        BottomButtonsContent(
            onClickFacebookButton = {
                event(LoginContractEvent.OnLoginViaFacebookClicked)
            },
            onClickGoogleButton = {
                event(LoginContractEvent.OnLoginViaGoogleClicked)
            },
            onClickSignUpButton = {
                event(LoginContractEvent.OnSignUpClicked)
            }
        )
        Spacer(modifier = Modifier.weight(0.061f))
    }
    LoadingDialog(
        isVisible = state.isScreenLoading,
        onDismiss = {
            event(LoginContractEvent.OnLoadingDismissWanted)
        }
    )
}

@Stable
@Composable
private fun ColumnScope.IntroContent() {
    Text(
        modifier = Modifier.padding(horizontal = 25.dp),
        text = stringResource(id = R.string.LoginPage_Title),
        style = StudyCardsTheme.typography.weight600Size23LineHeight24.copy(
            color = StudyCardsTheme.colors.textPrimary
        )
    )
    Spacer(modifier = Modifier.weight(0.014f))
    Text(
        text = stringResource(id = R.string.LoginPage_Subitle),
        style = StudyCardsTheme.typography.weight400Size20LineHeight20.copy(
            color = StudyCardsTheme.colors.textPrimary
        )
    )
}


@Composable
private fun ColumnScope.InputFieldsContent(
    email: String,
    password: String,
    onEnterEmailValue: (String) -> Unit,
    onEnterPasswordValue: (String) -> Unit
) {
    TextFieldWithLabel(
        label = stringResource(id = R.string.LoginPage_EmailTitle),
        hint = stringResource(id = R.string.LoginPage_EmailHint),
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
        label = stringResource(id = R.string.LoginPage_PasswordTitle),
        hint = stringResource(id = R.string.LoginPage_PasswordHint),
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
}

@Stable
@Composable
private fun ColumnScope.BottomButtonsContent(
    onClickGoogleButton: () -> Unit,
    onClickFacebookButton: () -> Unit,
    onClickSignUpButton: () -> Unit
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
            text = stringResource(id = R.string.LoginPage_AlternativeLogin),
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
            modifier = Modifier
                .height(50.dp)
                .weight(1f),
            iconRes = R.drawable.ic_google,
            onClick = onClickGoogleButton
        )
        IconShadowedButton(
            modifier = Modifier
                .height(50.dp)
                .weight(1f),
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
            onClick = onClickSignUpButton
        )
    }
}
