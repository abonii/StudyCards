package abm.co.feature.home

import abm.co.designsystem.component.modifier.Modifier
import abm.co.feature.userattributes.ChooseUserAttributesContractEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

@Composable
fun HomePage(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit){
        Firebase.analytics.logEvent(
            "home_page_viewed", null
        )
    }
    val state by viewModel.state.collectAsState()

    HomeScreen(
        state = state,
        event = viewModel::event
    )
}


@Composable
private fun HomeScreen(
    state: HomeContractState,
    event: (HomeContractEvent) -> Unit
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
                    .background(Color.Blue)
                    .fillMaxSize()
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Home",
                    color = Color.White
                )
            }
        }
    }
}
