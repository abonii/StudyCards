package abm.co.feature.login

import abm.co.designsystem.use
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

@Composable
fun LoginPage(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateRegistrationPage: () -> Unit,
    onNavigateHomePage: () -> Unit
) {
    val (state, event) = use(viewModel = viewModel)

    LoginScreen(
        newsListState = state,
        onNavigateHomePage = onNavigateHomePage
    )
}


@Composable
private fun LoginScreen(
    newsListState: LoginContract.State,
    onNavigateHomePage: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AnimatedVisibility(
            visible = !newsListState.refreshing,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .background(newsListState.color)
                    .fillMaxSize()
                    .clickable {
                        onNavigateHomePage()
                    }
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clickable {
                            onNavigateHomePage()
                        },
                    text = "Login",
                    color = Color.White
                )
            }
        }
    }
}
