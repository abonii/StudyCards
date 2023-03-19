package abm.co.feature.card.category

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.textfield.TextFieldWithLabel
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

@Composable
fun EditCategoryPage(
    viewModel: EditCategoryViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        Firebase.analytics.logEvent(
            "edit_set_of_cards_page_viewed", null
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
    state: EditCategoryContractState,
    event: (EditCategoryContractEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
        ) {
        Text(
            modifier = Modifier.padding(start = 27.dp, top = 30.dp),
            text = stringResource(id = R.string.Category_Toolbar_title),
            style = StudyCardsTheme.typography.weight600Size23LineHeight24,
            color = StudyCardsTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(30.dp))
        TextFieldWithLabel(
            label = stringResource(id = R.string.Category_Input_title),
            hint = stringResource(id = R.string.Category_Input_hint),
            value = state.categoryName,
            onValueChange = {
                event(EditCategoryContractEvent.OnEnterCategoryName(it))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 16.dp)
        )
    }
}
