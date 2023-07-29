package abm.co.feature.card.explorecategory

import abm.co.designsystem.base.SelectionHolder
import abm.co.designsystem.component.about.AboutView
import abm.co.designsystem.component.button.PrimaryButton
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.text.pluralString
import abm.co.designsystem.component.toolbar.Toolbar
import abm.co.designsystem.component.widget.LoadingView
import abm.co.designsystem.extensions.collectInLaunchedEffect
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.domain.functional.safeLet
import abm.co.feature.R
import abm.co.feature.card.model.CardUI
import abm.co.feature.userattributes.lanugage.LanguageUI
import abm.co.feature.utils.AnalyticsManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage

@Composable
fun ExploreCategoryPage(
    viewModel: ExploreCategoryViewModel = hiltViewModel(),
    showMessage: suspend (MessageContent) -> Unit,
    navigateBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        AnalyticsManager.sendEvent("explore_category_page_viewed")
    }
    val state by viewModel.state.collectAsState()

    viewModel.channel.collectInLaunchedEffect {
        when (it) {
            is ExploreCategoryContractChannel.NavigateBack -> navigateBack()
            is ExploreCategoryContractChannel.ShowMessage -> showMessage(it.messageContent)
            ExploreCategoryContractChannel.Share -> {
                // todo share
            }
        }
    }

    SetStatusBarColor()
    MainScreen(
        uiState = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun MainScreen(
    uiState: ExploreCategoryContractState,
    onEvent: (ExploreCategoryContractEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .background(StudyCardsTheme.colors.backgroundPrimary)
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        val lazyListState = rememberLazyListState()
        val fromLanguage = uiState.fromLang.collectAsStateWithLifecycle(initialValue = null)
        val toLanguage = uiState.toLang.collectAsStateWithLifecycle(initialValue = null)
        Toolbar(
            title = uiState.categoryTitle,
            onBack = {
                onEvent(ExploreCategoryContractEvent.OnBack)
            },
            lazyListState = lazyListState
        )
        when (val state = uiState.state) {
            ExploreCategoryContractState.State.Empty -> {
                EmptyScreen(
                    modifier = Modifier.weight(1f)
                )
            }

            ExploreCategoryContractState.State.Loading -> {
                Toolbar(
                    imageUrl = uiState.image,
                    fromLanguage = fromLanguage,
                    toLanguage = toLanguage,
                    modifier = Modifier.fillMaxWidth()
                )
                LoadingScreen(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }

            is ExploreCategoryContractState.State.Success -> {
                SuccessScreen(
                    uiState = state,
                    modifier = Modifier
                        .fillMaxWidth(),
                    onEvent = onEvent,
                    fromLanguage = fromLanguage,
                    toLanguage = toLanguage,
                    lazyListState = lazyListState
                )
            }
        }
    }
}

@Composable
private fun SuccessScreen(
    uiState: ExploreCategoryContractState.State.Success,
    onEvent: (ExploreCategoryContractEvent.Success) -> Unit,
    fromLanguage: State<LanguageUI?>,
    toLanguage: State<LanguageUI?>,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .weight(1f)
                .background(StudyCardsTheme.colors.backgroundSecondary),
            content = {
                item(
                    contentType = { "toolbar" },
                    content = {
                        Toolbar(
                            imageUrl = uiState.image,
                            fromLanguage = fromLanguage,
                            toLanguage = toLanguage,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                )
                item(
                    contentType = { "info" },
                    content = {
                        Info(
                            modifier = Modifier.fillParentMaxWidth(),
                            title = uiState.categoryUI.title,
                            wordCount = uiState.categoryUI.cardsCount,
                            description = uiState.categoryUI.description
                        )
                    }
                )
                cardItems(
                    items = uiState.cards,
                    onClickItem = {
                        onEvent(
                            ExploreCategoryContractEvent.Success.OnClickAddCard(it)
                        )
                    }
                )
            }
        )
        Divider(
            color = StudyCardsTheme.colors.stroke,
            thickness = 0.5.dp
        )
        PrimaryButton(
            title = when (val selectedCards = uiState.selectedCardsCount.value) {
                ExploreCategoryContractState.State.Success.SelectedCards.All -> {
                    stringResource(id = R.string.ExploreCategory_Button_addAll)
                }

                is ExploreCategoryContractState.State.Success.SelectedCards.Some -> {
                    stringResource(
                        id = R.string.ExploreCategory_Button_add,
                        selectedCards.count
                    )
                }
            },
            onClick = {
                onEvent(ExploreCategoryContractEvent.Success.OnClickAddPrimaryButton)
            },
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        LoadingView()
    }
}

@Composable
private fun EmptyScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.27f))
        Image(
            modifier = Modifier
                .weight(0.3f)
                .aspectRatio(1f),
            contentScale = ContentScale.Fit,
            painter = painterResource(id = R.drawable.illustration_card_empty),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.MainCard_Empty_title),
            style = StudyCardsTheme.typography.weight400Size16LineHeight24,
            color = StudyCardsTheme.colors.grayishBlue,
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(0.28f))
    }
}

@Composable
private fun Info(
    title: String,
    wordCount: Int,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = 8.dp)
            .background(
                color = StudyCardsTheme.colors.backgroundPrimary,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(top = 16.dp, bottom = 16.dp)
    ) {
        Text(
            text = title,
            color = StudyCardsTheme.colors.buttonPrimary,
            style = StudyCardsTheme.typography.weight600Size32LineHeight24,
            modifier = Modifier.padding(start = 16.dp, end = 32.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = pluralString(
                id = R.plurals.cards,
                count = wordCount
            ),
            color = StudyCardsTheme.colors.textPrimary,
            style = StudyCardsTheme.typography.weight600Size14LineHeight20,
            modifier = Modifier.padding(start = 16.dp, end = 42.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        AboutView(
            details = description,
            readAllText = stringResource(id = R.string.ExploreCategory_About_expand),
            collapseText = stringResource(id = R.string.ExploreCategory_About_collapse),
            textStyle = StudyCardsTheme.typography.weight400Size14LineHeight18.copy(
                color = StudyCardsTheme.colors.textSecondary
            ),
            modifier = Modifier.padding(start = 16.dp, end = 80.dp)
        )
    }
}

fun LazyListScope.cardItems(
    items: SnapshotStateList<SelectionHolder<CardUI>>,
    onClickItem: (SelectionHolder<CardUI>) -> Unit
) {
    items(
        items = items,
        key = { it.item.cardID },
        contentType = { "cards" },
        itemContent = { item ->
            CardItem(
                holder = item,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillParentMaxWidth(),
                onClick = {
                    onClickItem(item)
                }
            )
        }
    )
}

@Composable
private fun CardItem(
    holder: SelectionHolder<CardUI>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = StudyCardsTheme.colors.backgroundPrimary,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 13.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        val card = holder.item
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = card.name,
                color = StudyCardsTheme.colors.textPrimary,
                style = StudyCardsTheme.typography.weight600Size14LineHeight20,
            )
            Text(
                text = card.translation,
                color = StudyCardsTheme.colors.textSecondary,
                style = StudyCardsTheme.typography.weight600Size14LineHeight18,
            )
        }
        val selected = holder.isSelected
        Box(modifier = Modifier.clickableWithoutRipple(onClick)) {
            if (selected) {
                Image(
                    painter = painterResource(R.drawable.ic_selected),
                    colorFilter = ColorFilter.tint(
                        color = StudyCardsTheme.colors.success
                    ),
                    contentDescription = null
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.ic_select),
                    colorFilter = ColorFilter.tint(
                        color = StudyCardsTheme.colors.textSecondary
                    ),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun Toolbar(
    imageUrl: String?,
    fromLanguage: State<LanguageUI?>,
    toLanguage: State<LanguageUI?>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.background(StudyCardsTheme.colors.backgroundPrimary)) {
        AsyncImage(
            modifier = Modifier
                .height(160.dp)
                .clip(RoundedCornerShape(bottomEnd = 8.dp, bottomStart = 8.dp))
                .background(Color(0xFF_B8BDC8)),
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            placeholder = painterResource(id = R.drawable.illustration_our_cards_empty),
            error = painterResource(id = R.drawable.illustration_our_cards_empty)
        )
        safeLet(fromLanguage.value, toLanguage.value) { from, to ->
            Row(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = from.languageNameResCode),
                    color = StudyCardsTheme.colors.middleGray,
                    style = StudyCardsTheme.typography.weight600Size14LineHeight18,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    modifier = Modifier.size(28.dp),
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null,
                    tint = StudyCardsTheme.colors.middleGray
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = to.languageNameResCode),
                    color = StudyCardsTheme.colors.middleGray,
                    style = StudyCardsTheme.typography.weight600Size14LineHeight18,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
