package abm.co.feature.home

import abm.co.designsystem.use
import abm.co.feature.registration.RegistrationViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomePage(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val (state, event) = use(viewModel = viewModel)

    HomeScreen(
        newsListState = state,
        onRefresh = {
            event.invoke(HomeContract.Event.OnRefresh)
        }
    )
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun HomeScreen(
    newsListState: HomeContract.State,
    onRefresh: () -> Unit
) {
    val refreshState =
        rememberPullRefreshState(refreshing = newsListState.refreshing, onRefresh = onRefresh)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pullRefresh(refreshState)
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
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Home",
                    color = Color.White
                )
            }
        }
        PullRefreshIndicator(
            newsListState.refreshing,
            refreshState,
            Modifier.align(Alignment.TopCenter)
        )
    }
}
