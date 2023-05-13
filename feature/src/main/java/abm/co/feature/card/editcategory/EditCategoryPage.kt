package abm.co.feature.card.editcategory

import abm.co.designsystem.component.button.ButtonSize
import abm.co.designsystem.component.button.PrimaryButton
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.textfield.TextFieldWithLabel
import abm.co.designsystem.extensions.collectInLaunchedEffect
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.utils.AnalyticsManager
import abm.co.feature.utils.StudyCardsConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun EditCategoryPage(
    navigateBack: (CategoryUI?) -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: EditCategoryViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        AnalyticsManager.sendEvent("edit_category_page_viewed")
    }
    viewModel.channel.collectInLaunchedEffect {
        when (it) {
            is EditCategoryContractChannel.NavigateBack -> {
                navigateBack(it.value)
            }

            is EditCategoryContractChannel.ShowMessage -> showMessage(it.messageContent)
        }
    }
    val uiState by viewModel.state.collectAsState()
    SetStatusBarColor()
    Screen(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun Screen(
    uiState: EditCategoryContractState,
    onEvent: (EditCategoryContractEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .background(StudyCardsTheme.colors.backgroundPrimary)
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Toolbar(
            onBack = {
                onEvent(EditCategoryContractEvent.OnBack)
            }
        )
        ScrollableContent(
            title = uiState.title,
            onEnterTitle = {
                onEvent(EditCategoryContractEvent.OnEnterTitle(it))
            },
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            title = stringResource(id = R.string.EditCategory_SaveButton_title),
            components = ButtonSize(),
            onClick = {
                onEvent(EditCategoryContractEvent.OnClickSave)
            }
        )
    }
}

@Composable
private fun ScrollableContent(
    title: String,
    onEnterTitle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        TextFieldWithLabel(
            label = stringResource(id = R.string.EditCategory_TitleField_title),
            hint = stringResource(id = R.string.EditCategory_TitleField_hint),
            value = title,
            onValueChange = onEnterTitle,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(62.dp)
                .wrapContentWidth()
        )
    }
}


@Composable
private fun Toolbar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(StudyCardsConstants.TOOLBAR_HEIGHT)
            .fillMaxWidth()
            .padding(start = 6.dp, top = 6.dp, bottom = 6.dp, end = 16.dp)
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickableWithoutRipple(onBack)
                .padding(10.dp)
                .size(24.dp),
            painter = painterResource(id = R.drawable.ic_left),
            tint = StudyCardsTheme.colors.opposition,
            contentDescription = null
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(id = R.string.EditCategory_Toolbar_title),
            style = StudyCardsTheme.typography.weight600Size14LineHeight18,
            color = StudyCardsTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
