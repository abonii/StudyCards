package abm.co.feature.login

import abm.co.designsystem.collectInLaunchedEffect
import abm.co.designsystem.use
import abm.co.domain.base.Failure
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginPage(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateRegistrationPage: () -> Unit,
    onNavigateHomePage: () -> Unit,
    onNavigateToEmailPage: () -> Unit,
    onNavigateToForgotPage: () -> Unit,
    onFailure: (Failure) -> Unit
) {
    val (state, event, channel) = use(viewModel = viewModel)
    val startForResult = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (result.data != null) {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                    val account = task.getResult(ApiException::class.java)
                    account.idToken?.let { viewModel.firebaseAuthWithGoogle(it) }
                } catch (e: ApiException) {
                    viewModel.setLoading(false)
//                    e.message?.let { toast(it) }
                }
            }
        }
    }
    channel.collectInLaunchedEffect {
        when (it) {
            is LoginContract.Channel.LoginViaGoogle -> {
                startForResult.launch(it.intent)
            }
            LoginContract.Channel.NavigateToEmailFragment -> {
                onNavigateToEmailPage()
            }
            LoginContract.Channel.NavigateToForgotPassword -> {
                onNavigateToForgotPage()
            }
            LoginContract.Channel.NavigateToHome -> {
                onNavigateHomePage()
            }
            LoginContract.Channel.NavigateToRegistration -> {
                onNavigateRegistrationPage()
            }
            is LoginContract.Channel.ShowError -> onFailure(it.failure)
        }
    }

    LoginScreen(
        state = state,
        event = event
    )
}


@Composable
private fun LoginScreen(
    state: LoginContract.State,
    event: (LoginContract.Event) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Red)
                    .fillMaxSize()
                    .clickable {
                        event(LoginContract.Event.LoginViaGoogle)
                    }
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clickable {
                            event(LoginContract.Event.LoginViaGoogle)
                        },
                    text = "Login",
                    color = Color.White
                )
            }
        }
    }
}
