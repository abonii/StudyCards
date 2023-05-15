package abm.co.feature.card.selectcategory

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.text.pluralString
import abm.co.designsystem.component.toolbar.Toolbar
import abm.co.designsystem.component.widget.LoadingView
import abm.co.designsystem.extensions.collectInLaunchedEffect
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.utils.AnalyticsManager
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SelectCategoryPage(
    navigateBack: (CategoryUI?) -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: SelectCategoryViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        AnalyticsManager.sendEvent("select_category_page_viewed")
    }
    viewModel.channel.collectInLaunchedEffect {
        when (it) {
            SelectCategoryContractChannel.NavigateBack -> {
                navigateBack(null)
            }

            is SelectCategoryContractChannel.OnCategorySelected -> {
                navigateBack(it.value)
            }

            is SelectCategoryContractChannel.ShowMessage -> {
                showMessage(it.messageContent)
            }
        }
    }
    val uiState by viewModel.state.collectAsState()
    SetStatusBarColor()
    Screen(
        uiState = uiState,
        event = viewModel::onEvent
    )
}

@Composable
private fun Screen(
    uiState: SelectCategoryContractState,
    event: (SelectCategoryContractEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .background(StudyCardsTheme.colors.backgroundPrimary)
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Toolbar(
            title = stringResource(id = R.string.SelectCategory_Toolbar_title),
            onBack = {
                event(SelectCategoryContractEvent.OnBack)
            }
        )
        Crossfade(targetState = uiState, label = "SelectCategoryPage") { state ->
            when (state) {
                SelectCategoryContractState.Loading -> {
                    LoadingScreen(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }

                is SelectCategoryContractState.Empty -> {
                    EmptyScreen(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }

                is SelectCategoryContractState.Success -> {
                    SuccessScreen(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        screenState = state,
                        onClickCategory = {
                            event(SelectCategoryContractEvent.OnClickCategory(it))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SuccessScreen(
    screenState: SelectCategoryContractState.Success,
    onClickCategory: (CategoryUI) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        if (screenState.categories.isNotEmpty()) {
            item {
                Text(
                    modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                    text = stringResource(id = R.string.ChooseOrCreateCategory_Available_title),
                    style = StudyCardsTheme.typography.weight600Size16LineHeight18,
                    color = StudyCardsTheme.colors.textPrimary
                )
            }
        }
        items(
            items = screenState.categories,
            key = { it.id }
        ) { item ->
            CategoryItem(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                item = item,
                selected = item.id == screenState.selectedCategoryID,
                onClick = {
                    onClickCategory(item)
                }
            )
        }
    }
}

@Composable
private fun CategoryItem(
    item: CategoryUI,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(11.dp))
            .background(
                if (selected) {
                    StudyCardsTheme.colors.blueMiddle
                } else {
                    StudyCardsTheme.colors.milky
                }
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = item.title,
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
private fun EmptyScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.weight(0.4f))
        Text(
            modifier = Modifier
                .padding(horizontal = 36.dp)
                .fillMaxWidth(),
            text = stringResource(R.string.HomePage_Empty_title),
            style = StudyCardsTheme.typography.weight400Size14LineHeight24,
            color = StudyCardsTheme.colors.middleGray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.weight(0.4f))
        LoadingView(modifier = Modifier.align(CenterHorizontally))
        Spacer(modifier = Modifier.weight(0.6f))
    }
}
