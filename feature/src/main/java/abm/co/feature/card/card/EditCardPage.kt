package abm.co.feature.card.card

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
fun EditCardPage(
    viewModel: EditCardViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        Firebase.analytics.logEvent(
            "edit_card_page_viewed", null
        )
    }
    val state by viewModel.state.collectAsState()

    SetStatusBarColor()
    CategoryScreen(
        state = state,
        event = viewModel::event
    )
}


@Composable
private fun CategoryScreen(
    state: EditCardContractState,
    event: (EditCardContractEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .background(Color.Blue)
            .fillMaxSize()
    ) {

    }
}
