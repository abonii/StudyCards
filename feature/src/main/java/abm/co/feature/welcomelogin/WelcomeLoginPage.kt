package abm.co.feature.welcomelogin

import abm.co.designsystem.collectInLaunchedEffect
import abm.co.designsystem.component.button.ButtonComponents
import abm.co.designsystem.component.button.ButtonState
import abm.co.designsystem.component.button.PrimaryButton
import abm.co.designsystem.component.button.TextButton
import abm.co.designsystem.theme.StudyCardsTypography
import abm.co.designsystem.use
import abm.co.domain.base.Failure
import abm.co.feature.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import abm.co.designsystem.R as dR

@Composable
fun WelcomeLoginPage(
    viewModel: WelcomeLoginViewModel = hiltViewModel(),
    onNavigateRegistrationPage: () -> Unit,
    onNavigateHomePage: () -> Unit,
    onNavigateToLoginPage: () -> Unit,
    onFailure: (Failure) -> Unit
) {
    val (state, event, channel) = use(viewModel = viewModel)
    channel.collectInLaunchedEffect {
        when (it) {
            WelcomeLoginContract.Channel.NavigateToHomePage -> onNavigateHomePage()
            WelcomeLoginContract.Channel.NavigateToLoginPage -> onNavigateToLoginPage()
            WelcomeLoginContract.Channel.NavigateToRegistrationPage -> onNavigateRegistrationPage()
            is WelcomeLoginContract.Channel.OnFailure -> onFailure(it.failure)
        }
    }

    WelcomeLoginScreen(
        state = state,
        event = event
    )
}


@Composable
private fun WelcomeLoginScreen(
    state: WelcomeLoginContract.State,
    event: (WelcomeLoginContract.Event) -> Unit
) {
    Column(
        modifier = Modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colorResource(id = dR.color.colorPrimary).copy(alpha = .2f),
                        colorResource(id = dR.color.background),
                        colorResource(id = dR.color.background),
                        colorResource(id = dR.color.background),
                        colorResource(id = dR.color.background),
                        colorResource(id = dR.color.background),
                        colorResource(id = dR.color.colorPrimary).copy(alpha = .2f)
                    )
                )
            )
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
            text = "Create Account",
            style = StudyCardsTypography.wight600Size23LineHeight24.copy(
                color = colorResource(id = dR.color.text_primary)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            modifier = Modifier.padding(horizontal = 15.dp),
            text = "to get to started now",
            style = StudyCardsTypography.wight400Size20LineHeight20.copy(
                color = colorResource(id = dR.color.text_primary)
            )
        )
        Spacer(modifier = Modifier.weight(0.067f))
        PrimaryButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            title = "LOGIN", // TODO
            buttonState = if (state.isLoading) ButtonState.Loading else ButtonState.Normal,
            onClick = {
                event(WelcomeLoginContract.Event.OnClickLogin)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            title = "SIGN UP", // TODO
            components = ButtonComponents(
                normalButtonBackgroundColor = dR.color.button_item_secondary,
                normalContentColor = dR.color.button_item_text_button
            ),
            buttonState = if (state.isLoading) ButtonState.Loading else ButtonState.Normal,
            onClick = {
                event(WelcomeLoginContract.Event.OnClickLogin)
            }
        )
        Spacer(modifier = Modifier.weight(0.157f))
        TextButton(
            title = "Continue as a guest", // TODO
            components = ButtonComponents(
                normalContentColor = dR.color.button_item_text_button
            ),
            buttonState = if (state.isLoading) ButtonState.Loading else ButtonState.Normal,
            onClick = {
                event(WelcomeLoginContract.Event.OnClickLogin)
            }
        )
        Spacer(modifier = Modifier.weight(0.06f))
    }
}
