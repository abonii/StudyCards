package abm.co.feature.card.main

import abm.co.designsystem.component.button.PrimaryButton
import abm.co.designsystem.component.button.SecondaryButton
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.baseBackground
import abm.co.designsystem.component.modifier.clickableWithoutRipple
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
        contentPadding = PaddingValues(
            vertical = 24.dp
        ),
        modifier = modifier,
        content = {
            if (ourCategories.isNotEmpty()) {
                item(
                    contentType = "ourCategories"
                ) {
                    OurSet(
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .fillMaxWidth(),
                        items = ourCategories,
                        onClickItem = {
                            // todo
                        },
                        onClickItemPlay = {
                            onClickPlayCategory(it)
                        }
                    )
                }
            }
            item(
                contentType = "userCategoriesTitle"
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 10.dp),
                    text = stringResource(id = R.string.MainCard_YourSet_title),
                    style = StudyCardsTheme.typography.weight600Size12LineHeight20,
                    color = StudyCardsTheme.colors.textPrimary
                )
            }
            if (userCategories.isNotEmpty()) {
                items(
                    items = userCategories,
                    contentType = {
                        "userCategories"
                    }
                ) { item ->
                    CategoryItem(
                        title = item.title,
                        isBookmarked = item.bookmarked,
                        subtitle = pluralString(
                            id = R.plurals.cards,
                            item.cardsCount.takeIf { it > 0 } ?: 0
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
                        isPublished = item.published,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 10.dp)
                    )
                }
            } else {
                item(
                    contentType = "userCategoriesEmpty"
                ) {
                    EmptyCategories(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
        }
    )
}

@Composable
private fun OurSet(
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
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = title,
            style = StudyCardsTheme.typography.weight600Size12LineHeight20,
            color = StudyCardsTheme.colors.textPrimary
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            content = {
                items(
                    items = items,
                    key = { it.id }
                ) { item ->
                    OurSetItem(
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
                        }
                    )
                }
            }
        )
    }
}

@Composable
private fun OurSetItem(
    image: String?,
    name: String,
    wordCount: String,
    onClick: () -> Unit,
    onClickPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickableWithoutRipple(onClick)
            .background(
                color = StudyCardsTheme.colors.grayishBlue,
                shape = RoundedCornerShape(11.dp)
            )
            .height(156.dp)
            .aspectRatio(2.2f)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(StudyCardsTheme.colors.middleGray)
                .weight(0.62f)
                .fillMaxWidth()
        ) {
            AsyncImage(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .matchParentSize(),
                model = image,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                error = painterResource(id = R.drawable.illustration_our_cards_empty)
            )
        }
        Row(
            modifier = Modifier.weight(0.38f),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = name,
                    style = StudyCardsTheme.typography.weight500Size16LineHeight20,
                    color = Color.White
                )
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = wordCount,
                    style = StudyCardsTheme.typography.weight400Size16LineHeight20,
                    color = Color.White
                )
            }
            Icon(
                modifier = Modifier
                    .clickableWithoutRipple(onClick = onClickPlay)
                    .padding(top = 10.dp, bottom = 10.dp)
                    .size(24.dp),
                painter = painterResource(id = R.drawable.ic_play),
                tint = StudyCardsTheme.colors.buttonPrimary,
                contentDescription = null
            )
        }
    }
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
