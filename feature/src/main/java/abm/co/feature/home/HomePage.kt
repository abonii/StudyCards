package abm.co.feature.home

import abm.co.designsystem.component.button.TextButton
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.animateDp
import abm.co.designsystem.component.modifier.baseBackground
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.widget.LoadingView
import abm.co.designsystem.extensions.collectInLaunchedEffect
import abm.co.designsystem.extensions.getActivity
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.card.component.CategoryItem
import abm.co.feature.card.model.CategoryUI
import abm.co.feature.home.component.HomeCollapsingToolbar
import abm.co.feature.toolbar.ToolbarState
import abm.co.feature.toolbar.rememberToolbarState
import abm.co.feature.utils.AnalyticsManager
import abm.co.permissions.extension.requestPushNotificationsPermission
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

private val MinToolbarHeight = 90.dp
private val MaxToolbarHeight = 174.dp

@Composable
fun HomePage(
    openDrawer: suspend () -> Unit,
    onNavigateToLanguageSelectPage: () -> Unit,
    navigateToAllCategory: () -> Unit,
    navigateToGameKinds: (CategoryUI) -> Unit,
    navigateToCategory: (CategoryUI) -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        AnalyticsManager.sendEvent("home_page_viewed")
    }
    val activity = getActivity()
    LaunchedEffect(Unit) {
        activity?.requestPushNotificationsPermission()
    }
    viewModel.channel.collectInLaunchedEffect {
        when (it) {
            HomeContractChannel.NavigateToLanguageSelectPage -> onNavigateToLanguageSelectPage()
            HomeContractChannel.OpenDrawer -> openDrawer()
            HomeContractChannel.NavigateToAllCategory -> navigateToAllCategory()
            is HomeContractChannel.ShowMessage -> showMessage(it.messageContent)
            is HomeContractChannel.NavigateToGameKinds -> navigateToGameKinds(it.value)
            is HomeContractChannel.NavigateToCategory -> navigateToCategory(it.category)
        }
    }
    val screenState by viewModel.screenState.collectAsState()
    val toolbarState by viewModel.toolbarState.collectAsState()
    SetStatusBarColor()
    HomeScreen(
        screenState = screenState,
        toolbarState = toolbarState,
        event = viewModel::onEvent
    )
}

@Composable
private fun HomeScreen(
    screenState: HomeContract.ScreenState,
    toolbarState: HomeContract.ToolbarState,
    event: (HomeContractEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .baseBackground()
            .fillMaxSize()
    ) {
        val toolbarScrollable = rememberToolbarState(
            minHeight = MinToolbarHeight,
            maxHeight = MaxToolbarHeight
        )
        val scrollState = rememberScrollState()
        ListenToScrollAndUpdateToolbarState(
            scrollState = scrollState,
            toolbarState = toolbarScrollable
        )
        Crossfade(targetState = screenState) { state ->
            when (state) {
                HomeContract.ScreenState.Loading -> {
                    LoadingScreen(modifier = Modifier.fillMaxSize())
                }

                is HomeContract.ScreenState.Empty -> {
                    EmptyScreen(modifier = Modifier.fillMaxSize())
                }

                is HomeContract.ScreenState.Success -> {
                    SuccessScreen(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .fillMaxSize(),
                        screenState = state,
                        onClickPlayCategory = {
                            event(HomeContractEvent.OnClickPlayCategory(it))
                        },
                        onClickCategory = {
                            event(HomeContractEvent.OnClickCategory(it))
                        },
                        onClickShowAllCategory = {
                            event(HomeContractEvent.OnClickShowAllCategory)
                        },
                        onClickBookmark = {
                            event(HomeContractEvent.OnClickBookmarkCategory(it))
                        }
                    )
                }
            }
        }
        HomeCollapsingToolbar(
            toolbarTitle = stringResource(id = R.string.HomePage_Toolbar_title),
            learningLanguageText = stringResource(R.string.HomePage_Toolbar_learningLanguage) + " " +
                    stringResource(
                        toolbarState.learningLanguage?.languageNameResCode
                            ?: R.string.HomePage_Toolbar_language
                    ),
            learningLanguageRes = toolbarState.learningLanguage?.flagFromDrawable,
            welcomeText = stringResource(id = R.string.HomePage_Toolbar_welcome) + ", " +
                    (toolbarState.userName ?: stringResource(id = R.string.HomePage_Toolbar_guest)),
            progress = toolbarScrollable.progress,
            onClickDrawerIcon = { event(HomeContractEvent.OnClickDrawer) },
            onClickLearningLanguageIcon = { event(HomeContractEvent.OnClickToolbarLanguage) },
            modifier = Modifier
                .fillMaxWidth()
                .height(with(LocalDensity.current) { toolbarScrollable.height.toDp() })
                .graphicsLayer { translationY = toolbarScrollable.offset }
        )
    }
}

@Composable
private fun SuccessScreen(
    screenState: HomeContract.ScreenState.Success,
    onClickShowAllCategory: () -> Unit,
    onClickCategory: (CategoryUI) -> Unit,
    onClickBookmark: (CategoryUI) -> Unit,
    onClickPlayCategory: (CategoryUI) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = MaxToolbarHeight)
            .statusBarsPadding()
    ) {
        ItemTitle(
            title = stringResource(id = R.string.HomePage_Category_title),
            onClickShowAll = null // todo implement show all categories
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            screenState.setsOfCards.forEach { category ->
                key(category.id) {
                    CategoryItem(
                        category = category,
                        onClick = { onClickCategory(category) },
                        onClickBookmark = { onClickBookmark(category) },
                        onClickPlay = { onClickPlayCategory(category) },
                    )
                }
            }
        }
    }
}

@Stable
@Composable
private fun ItemTitle(
    title: String,
    modifier: Modifier = Modifier,
    onClickShowAll: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            text = title,
            style = StudyCardsTheme.typography.weight600Size14LineHeight20,
            color = StudyCardsTheme.colors.textPrimary
        )
        onClickShowAll?.let {
            TextButton(
                modifier = Modifier.padding(end = 16.dp),
                title = stringResource(id = R.string.HomePage_Category_showAll),
                textStyle = StudyCardsTheme.typography.weight600Size14LineHeight20,
                normalContentColor = StudyCardsTheme.colors.buttonPrimary,
                onClick = it
            )
        }
    }
}

@Composable
private fun EmptyScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = MaxToolbarHeight)
            .statusBarsPadding()
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
        AnimatableArrow(
            modifier = Modifier
                .weight(0.6f)
                .align(CenterHorizontally)
        )
    }
}

@Composable
private fun AnimatableArrow(
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val infiniteTransition = rememberInfiniteTransition()
        val offsetY by infiniteTransition.animateDp(
            initialValue = maxHeight - 53.dp * 3,
            targetValue = maxHeight - 53.dp,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1400),
                repeatMode = RepeatMode.Reverse,
            )
        )
        Icon(
            modifier = Modifier
                .size(53.dp)
                .offset(y = offsetY),
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_down),
            tint = StudyCardsTheme.colors.middleGray,
            contentDescription = null
        )
    }
}

@Composable
private fun ListenToScrollAndUpdateToolbarState(
    scrollState: ScrollState,
    toolbarState: ToolbarState
) {
    LaunchedEffect(scrollState.value) {
        toolbarState.scrollValue = scrollState.value
    }
}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = MaxToolbarHeight)
            .statusBarsPadding()
    ) {
        Spacer(modifier = Modifier.weight(0.4f))
        LoadingView(modifier = Modifier.align(CenterHorizontally))
        Spacer(modifier = Modifier.weight(0.6f))
    }
}
