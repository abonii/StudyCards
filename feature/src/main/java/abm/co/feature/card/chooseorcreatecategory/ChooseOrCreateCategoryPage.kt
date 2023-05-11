package abm.co.feature.card.chooseorcreatecategory

import abm.co.designsystem.component.button.PrimaryButton
import abm.co.designsystem.component.button.SecondaryButton
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.text.pluralString
import abm.co.designsystem.component.textfield.TextFieldWithLabel
import abm.co.designsystem.component.widget.LinearProgress
import abm.co.designsystem.extensions.collectInLaunchedEffect
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.card.model.CategoryUI
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChooseOrCreateCategoryPage(
    onBack: () -> Unit,
    navigateToNewCard: (CategoryUI) -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: ChooseOrCreateCategoryViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        Firebase.analytics.logEvent(
            "edit_category_page_viewed", null
        )
    }
    val state by viewModel.state.collectAsState()

    viewModel.channel.collectInLaunchedEffect {
        when (it) {
            ChooseOrCreateCategoryContractChannel.NavigateBack -> onBack()
            is ChooseOrCreateCategoryContractChannel.NavigateToNewCard -> navigateToNewCard(it.category)
            is ChooseOrCreateCategoryContractChannel.ShowMessage -> showMessage(it.messageContent)
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    DisposableEffect(Unit) {
        onDispose {
            keyboardController?.hide()
        }
    }

    SetStatusBarColor()
    CategoryScreen(
        uiState = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun CategoryScreen(
    uiState: ChooseOrCreateCategoryContractState,
    onEvent: (ChooseOrCreateCategoryContractEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Toolbar(
            categoryName = uiState.categoryName,
            onEnterCategoryName = {
                onEvent(ChooseOrCreateCategoryContractEvent.OnEnterCategoryName(it))
            },
            onBack = {
                onEvent(ChooseOrCreateCategoryContractEvent.OnBackClicked)
            },
            middleContent = {
                uiState.progress?.let { progress ->
                    LinearProgress(
                        progressFloat = progress,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .height(6.dp)
                            .fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(29.dp))
                }
            }
        )
        ScrollableContent(
            modifier = Modifier.weight(1f),
            onClickItem = {
                onEvent(ChooseOrCreateCategoryContractEvent.OnCategoryClicked(it))
            },
            items = uiState.categories
        )
        Buttons(
            modifier = Modifier.padding(bottom = 15.dp, start = 20.dp, end = 20.dp, top = 10.dp),
            onClickPrimary = {
                onEvent(ChooseOrCreateCategoryContractEvent.OnContinue)
            },
            onClickSecondary = {
                onEvent(ChooseOrCreateCategoryContractEvent.OnBackClicked)
            }
        )
    }
}

@Composable
private fun Buttons(
    onClickPrimary: () -> Unit,
    onClickSecondary: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PrimaryButton(
            modifier = Modifier
                .fillMaxWidth(),
            title = stringResource(id = R.string.Category_Button_title),
            onClick = onClickPrimary
        )
        SecondaryButton(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(id = R.string.EditCard_Button_back),
            onClick = onClickSecondary
        )
    }
}

@Composable
private fun ScrollableContent(
    items: List<CategoryUI>,
    onClickItem: (CategoryUI) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                text = stringResource(id = R.string.ChooseOrCreateCategory_Available_title),
                style = StudyCardsTheme.typography.weight600Size16LineHeight18,
                color = StudyCardsTheme.colors.textPrimary
            )
        }
        items(
            items = items,
            key = { it.id }
        ) { item ->
            CategoryItem(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                item = item,
                onClick = {
                    onClickItem(item)
                }
            )
        }
    }
}

@Composable
private fun CategoryItem(
    item: CategoryUI,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(11.dp))
            .background(StudyCardsTheme.colors.milky)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = item.name,
            style = StudyCardsTheme.typography.weight500Size16LineHeight20,
            color = StudyCardsTheme.colors.textPrimary
        )
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = pluralString(
                id = R.plurals.cards,
                item.cardsCount.takeIf { it > 0 } ?: 0
            ),
            style = StudyCardsTheme.typography.weight400Size16LineHeight20,
            color = StudyCardsTheme.colors.grayishBlack
        )
    }
}

@Composable
private fun Toolbar(
    categoryName: String,
    onEnterCategoryName: (String) -> Unit,
    onBack: () -> Unit,
    middleContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
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
        middleContent()
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
