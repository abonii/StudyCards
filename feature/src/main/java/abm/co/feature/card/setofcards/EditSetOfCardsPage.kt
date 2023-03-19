package abm.co.feature.card.setofcards

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.systembar.SetStatusBarColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

@Composable
fun EditSetOfCardsPage(
    viewModel: EditSetOfCardsViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        Firebase.analytics.logEvent(
            "edit_set_of_cards_page_viewed", null
        )
    }
    val state by viewModel.state.collectAsState()

    SetStatusBarColor()
    SetOfCardsScreen(
        state = state,
        event = viewModel::event
    )
}


@Composable
private fun SetOfCardsScreen(
    state: EditSetOfCardsContractState,
    event: (EditSetOfCardsContractEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .background(Color.Green)
            .fillMaxSize()
    ) {

    }
}
