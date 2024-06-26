package abm.co.feature.authorization.welcomelogin

import abm.co.designsystem.extensions.collectInLaunchedEffect
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.button.ButtonSize
import abm.co.designsystem.component.button.ButtonState
import abm.co.designsystem.component.button.PrimaryButton
import abm.co.designsystem.component.button.TextButton
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.baseBackground
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.utils.AnalyticsManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun WelcomeLoginPage(
    onNavigateSignUpPage: () -> Unit,
    onNavigateChooseUserAttributes: () -> Unit,
    onNavigateToLoginPage: () -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: WelcomeLoginViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit){
        AnalyticsManager.sendEvent("welcome_login_page_viewed")
    }
    val state by viewModel.state.collectAsState()
    viewModel.channel.collectInLaunchedEffect {
        when (it) {
            WelcomeLoginContractChannel.NavigateToChooseUserAttributes -> onNavigateChooseUserAttributes()
            WelcomeLoginContractChannel.NavigateToLoginPage -> onNavigateToLoginPage()
            WelcomeLoginContractChannel.NavigateToSignUpPage -> onNavigateSignUpPage()
            is WelcomeLoginContractChannel.ShowMessage -> showMessage(it.messageContent)
        }
    }
    SetStatusBarColor()
    WelcomeLoginScreen(
        state = state,
        event = viewModel::event
    )
}


@Composable
private fun WelcomeLoginScreen(
    state: WelcomeLoginContractState,
    event: (WelcomeLoginContractEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .navigationBarsPadding()
            .baseBackground()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.17f))
        Image(
            modifier = Modifier.weight(0.19f),
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.weight(0.11f))
        Text(
            modifier = Modifier.padding(horizontal = 15.dp),
            text = stringResource(id = R.string.WelcomeLoginPage_Title),
            style = StudyCardsTheme.typography.weight600Size23LineHeight24.copy(
                color = StudyCardsTheme.colors.textPrimary
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            modifier = Modifier.padding(horizontal = 15.dp),
            text = stringResource(id = R.string.WelcomeLoginPage_Subtitle),
            style = StudyCardsTheme.typography.weight400Size20LineHeight20.copy(
                color = StudyCardsTheme.colors.textPrimary
            )
        )
        Spacer(modifier = Modifier.weight(0.067f))
        PrimaryButton(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            title = stringResource(id = R.string.WelcomeLoginPage_SignUpButton),
            onClick = {
                event(WelcomeLoginContractEvent.OnClickSignUp)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            title = stringResource(id = R.string.WelcomeLoginPage_LoginButton),
            components = ButtonSize(),
            normalButtonBackgroundColor = StudyCardsTheme.colors.blueMiddle,
            normalContentColor = Color.White,
            onClick = {
                event(WelcomeLoginContractEvent.OnClickLogin)
            }
        )
        Spacer(modifier = Modifier.weight(0.157f))
        TextButton(
            title = stringResource(id = R.string.WelcomeLoginPage_LoginAsGuest),
            components = ButtonSize(),
            buttonState = if (state.isLoading) ButtonState.Loading else ButtonState.Normal,
            onClick = {
                event(WelcomeLoginContractEvent.OnClickLoginAsGuest)
            }
        )
        Spacer(modifier = Modifier.weight(0.06f))
    }
}
