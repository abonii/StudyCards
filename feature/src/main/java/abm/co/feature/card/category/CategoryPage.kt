package abm.co.feature.card.category

import abm.co.designsystem.component.button.PrimaryButton
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.textfield.TextFieldWithLabel
import abm.co.designsystem.flow.collectInLaunchedEffect
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

@Composable
fun CategoryPage(
    onBack: () -> Unit,
    navigateToNewCard: () -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: CategoryViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        Firebase.analytics.logEvent(
            "category_page_viewed", null
        )
    }
    val state by viewModel.state.collectAsState()

    viewModel.channel.collectInLaunchedEffect {
        when (it) {
            CategoryContractChannel.NavigateBack -> onBack()
            CategoryContractChannel.NavigateToNewCard -> navigateToNewCard()
        }
    }

    SetStatusBarColor()
    CategoryScreen(
        state = state,
        event = viewModel::event
    )
}

@Composable
private fun CategoryScreen(
    state: CategoryContractState,
    event: (CategoryContractEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Toolbar(
            categoryName = state.categoryName,
            onEnterCategoryName = {
                event(CategoryContractEvent.OnEnterCategoryName(it))
            },
            onBack = {
                event(CategoryContractEvent.OnBackClicked)
            }
        )
        ScrollableContent(modifier = Modifier.weight(1f))
        PrimaryButton(
            modifier = Modifier
                .padding(bottom = 30.dp, start = 20.dp, end = 20.dp, top = 10.dp)
                .navigationBarsPadding()
                .fillMaxWidth(),
            title = stringResource(id = R.string.Category_Button_title),
            onClick = {
                event(CategoryContractEvent.OnContinueButtonClicked)
            }
        )
    }
}

@Composable
private fun Toolbar(
    categoryName: String,
    onEnterCategoryName: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(start = 6.dp, top = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .clickableWithoutRipple(onBack)
                    .padding(10.dp)
                    .size(24.dp),
                painter = painterResource(id = R.drawable.ic_left),
                contentDescription = null
            )
            Text(
                text = stringResource(id = R.string.Category_Toolbar_title),
                style = StudyCardsTheme.typography.weight600Size23LineHeight24,
                color = StudyCardsTheme.colors.textPrimary
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        TextFieldWithLabel(
            label = stringResource(id = R.string.Category_Input_title),
            hint = stringResource(id = R.string.Category_Input_hint),
            value = categoryName,
            onValueChange = onEnterCategoryName,
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun ScrollableContent(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier)
}
