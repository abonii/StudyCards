package abm.co.feature.card.main

import abm.co.designsystem.component.button.PrimaryButton
import abm.co.designsystem.component.button.SecondaryButton
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.baseBackground
import abm.co.designsystem.component.modifier.scalableClick
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.text.pluralString
import abm.co.designsystem.component.widget.LoadingView
import abm.co.designsystem.extensions.collectInLaunchedEffect
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.card.component.CategoryItem
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.utils.AnalyticsManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun MainCardPage(
    viewModel: MainCardViewModel = hiltViewModel(),
    showMessage: suspend (MessageContent) -> Unit,
    navigateToLearnGame: (CategoryUI) -> Unit,
) {
    LaunchedEffect(Unit) {
        AnalyticsManager.sendEvent("card_page_viewed")
    }
    val state by viewModel.state.collectAsState()

    viewModel.channel.collectInLaunchedEffect {
        when (it) {
            is MainCardContractChannel.NavigateToLearnGame -> navigateToLearnGame(it.item)
            is MainCardContractChannel.ShowMessage -> showMessage(it.messageContent)
        }
    }

    SetStatusBarColor()
    MainScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun MainScreen(
    state: MainCardContractState,
    onEvent: (MainCardContractEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .baseBackground()
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Toolbar()
        when (state) {
            MainCardContractState.Empty -> {
                EmptyScreen(
                    modifier = Modifier.weight(1f)
                )
            }

            MainCardContractState.Loading -> {
                LoadingScreen(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }

            is MainCardContractState.Success -> {
                ScrollableContent(
                    modifier = Modifier.weight(1f),
                    ourCategories = state.ourCategories,
                    userCategories = state.userCategories,
                    onClickBookmark = {},
                    onClickPlayCategory = {
                        onEvent(MainCardContractEvent.OnClickCategoryPlay(it))
                    },
                    onClickShareCategory = {
                        onEvent(MainCardContractEvent.OnClickCategoryShare(it))
                    }
                )
                state.categoryConfirmShare?.let {
                    DialogContent(
                        shareCategory = it,
                        onDismiss = {
                            onEvent(MainCardContractEvent.OnClickCategoryConfirmClose)
                        },
                        onPrimaryButtonClicked = {
                            onEvent(MainCardContractEvent.OnClickCategoryConfirmShare(it))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScrollableContent(
    modifier: Modifier = Modifier,
    ourCategories: List<CategoryUI>,
    userCategories: List<CategoryUI>,
    onClickShareCategory: (CategoryUI) -> Unit,
    onClickPlayCategory: (CategoryUI) -> Unit,
    onClickBookmark: (CategoryUI) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 24.dp),
        modifier = modifier,
        content = {
            if (ourCategories.isNotEmpty()) {
                item(contentType = "ourCategories") {
                    OurRecommendedSet(
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .fillMaxWidth(),
                        items = ourCategories,
                        onClickItem = onClickPlayCategory,
                        onClickItemPlay = onClickPlayCategory
                    )
                }
            }
            if (userCategories.isNotEmpty()) {
                item(contentType = { "userCategories" }) {
                    SetOfCategories(
                        userCategories = userCategories,
                        onClickShareCategory = onClickShareCategory,
                        onClickPlayCategory = onClickPlayCategory,
                        onClickBookmark = onClickBookmark
                    )
                }
            } else {
                item(contentType = "userCategoriesEmpty") {
                    EmptyCategories(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
        }
    )
}

@Composable
private fun OurRecommendedSet(
    items: List<CategoryUI>,
    onClickItem: (CategoryUI) -> Unit,
    onClickItemPlay: (CategoryUI) -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.MainCard_OurSet_title)
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Title(
            modifier = Modifier.padding(horizontal = 16.dp),
            title = title
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            content = {
                items(
                    items = items,
                    key = { it.id }
                ) { item ->
                    OurRecommendedSetItem(
                        image = item.imageURL,
                        wordCount = pluralString(
                            id = R.plurals.cards,
                            item.cardsCount.takeIf { it > 0 } ?: 0
                        ),
                        name = item.title,
                        onClick = {
                            onClickItem(item)
                        },
                        onClickPlay = {
                            onClickItemPlay(item)
                        },
                        modifier = Modifier.fillParentMaxWidth()
                    )
                }
            }
        )
    }
}

@Composable
private fun LazyItemScope.SetOfCategories(
    userCategories: List<CategoryUI>,
    onClickShareCategory: (CategoryUI) -> Unit,
    onClickPlayCategory: (CategoryUI) -> Unit,
    onClickBookmark: (CategoryUI) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Title(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 10.dp),
            title = stringResource(id = R.string.MainCard_YourSet_title)
        )
        Row(
            modifier = Modifier
                .fillParentMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(19.dp)
        ) {
            val chunkedItems = remember(userCategories) {
                userCategories.chunked(2)
            }
            chunkedItems.forEach { columnItems ->
                Column(
                    modifier = Modifier.fillParentMaxWidth(0.8f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    columnItems.forEach { item ->
                        CategoryItem(
                            title = item.title,
                            isBookmarked = item.bookmarked,
                            subtitle = pluralString(
                                id = R.plurals.cards,
                                count = remember(item.cardsCount) {
                                    item.cardsCount.takeIf { it > 0 } ?: 0
                                }
                            ),
                            onClickPlay = {
                                onClickPlayCategory(item)
                            },
                            onClickBookmark = {
                                onClickBookmark(item)
                            },
                            onClickShare = {
                                onClickShareCategory(item)
                            },
                            isPublished = item.published
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OurRecommendedSetItem(
    image: String?,
    name: String,
    wordCount: String,
    onClick: () -> Unit,
    onClickPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    Box {
        val pressed = rememberSaveable { mutableStateOf(false) }
        val scale = animateFloatAsState(if (pressed.value) 0.95f else 1f)
        Spacer(
            modifier = Modifier
                .scalableClick(
                    pressed = pressed,
                    onClick = onClick
                )
                .matchParentSize()
        )
        Column(
            modifier = modifier
                .scale(scale.value)
                .shadow(
                    elevation = 5.dp,
                    shape = shape,
                    ambientColor = Color.Transparent,
                    spotColor = StudyCardsTheme.colors.opposition
                )
                .background(
                    color = StudyCardsTheme.colors.backgroundPrimary,
                    shape = shape
                )
                .padding(12.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(shape)
                    .height(74.dp)
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    modifier = Modifier
                        .clip(shape)
                        .matchParentSize(),
                    model = image,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    error = painterResource(id = R.drawable.illustration_our_cards_empty)
                )
            }
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(end = 16.dp),
                        text = name,
                        style = StudyCardsTheme.typography.weight500Size16LineHeight20,
                        color = StudyCardsTheme.colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        modifier = Modifier.padding(end = 16.dp),
                        text = wordCount,
                        style = StudyCardsTheme.typography.weight400Size16LineHeight20,
                        color = StudyCardsTheme.colors.textSecondary
                    )
                }
                Icon(
                    modifier = Modifier
                        .scalableClick(
                            pressed = pressed,
                            onClick = onClickPlay
                        )
                        .padding(top = 10.dp, bottom = 10.dp)
                        .size(24.dp),
                    painter = painterResource(id = R.drawable.ic_play),
                    tint = StudyCardsTheme.colors.buttonPrimary,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun Title(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = title,
        style = StudyCardsTheme.typography.weight600Size12LineHeight20,
        color = StudyCardsTheme.colors.textPrimary
    )
}

@Composable
private fun Toolbar(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.MainCard_Toolbar_title),
            style = StudyCardsTheme.typography.weight600Size14LineHeight18,
            color = StudyCardsTheme.colors.textPrimary,
            modifier = Modifier.align(Alignment.Center)
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
private fun EmptyCategories(
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(id = R.string.MainCard_Empty_title),
        style = StudyCardsTheme.typography.weight400Size16LineHeight24,
        color = StudyCardsTheme.colors.grayishBlue,
        modifier = modifier.padding(horizontal = 16.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun DialogContent(
    shareCategory: CategoryUI,
    onDismiss: () -> Unit,
    onPrimaryButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        content = {
            Column(
                modifier = modifier
                    .background(
                        color = StudyCardsTheme.colors.backgroundPrimary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(
                        horizontal = 16.dp,
                        vertical = 24.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.MainCard_Share_title),
                    style = StudyCardsTheme.typography.weight600Size16LineHeight18,
                    color = StudyCardsTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(id = R.string.MainCard_Share_subtitle),
                    style = StudyCardsTheme.typography.weight400Size12LineHeight16,
                    color = StudyCardsTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(24.dp))
                CategoryItem(
                    isBookmarked = shareCategory.bookmarked,
                    title = shareCategory.title,
                    subtitle = pluralString(
                        id = R.plurals.cards,
                        shareCategory.cardsCount.takeIf { it > 0 } ?: 0
                    )
                )
                Spacer(modifier = Modifier.height(32.dp))
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(
                        id = if (shareCategory.published == true) R.string.MainCard_Share_Button_NotPublish_title
                        else R.string.MainCard_Share_Button_Publish_title
                    ),
                    onClick = onPrimaryButtonClicked
                )
                Spacer(modifier = Modifier.height(12.dp))
                SecondaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(id = R.string.MainCard_Share_Button_secondary),
                    onClick = onDismiss
                )
            }
        }
    )
}
