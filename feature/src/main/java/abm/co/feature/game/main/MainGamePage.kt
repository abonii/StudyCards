package abm.co.feature.game.main

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.systembar.SetStatusBarColor
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

@Composable
fun MainGamePage(
    viewModel: MainGameViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        Firebase.analytics.logEvent(
            "card_page_viewed", null
        )
    }
    val state by viewModel.state.collectAsState()

    SetStatusBarColor()
    GameScreen(
        state = state,
        event = viewModel::event
    )
}


@Composable
private fun GameScreen(
    state: MainGameContractState,
    event: (MainGameContractEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {

    }
}
