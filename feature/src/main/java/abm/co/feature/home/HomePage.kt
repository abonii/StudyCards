package abm.co.feature.home

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.animateDp
import abm.co.designsystem.component.modifier.baseBackground
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.component.widget.LoadingView
import abm.co.designsystem.flow.collectInLaunchedEffect
import abm.co.designsystem.message.common.MessageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.card.component.SetOfCardsItem
import abm.co.feature.card.model.SetOfCardsUI
import abm.co.feature.home.component.HomeCollapsingToolbar
import abm.co.feature.toolbar.ToolbarState
import abm.co.feature.toolbar.scrollflags.ExitUntilCollapsedState
import abm.co.feature.utils.AnalyticsManager
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

private val MinToolbarHeight = 90.dp
private val MaxToolbarHeight = 174.dp

@Composable
fun HomePage(
    openDrawer: () -> Unit,
    onNavigateToLanguageSelectPage: () -> Unit,
    showMessage: suspend (MessageContent) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        AnalyticsManager.sendEvent("home_page_viewed")
    }
    val state by viewModel.state.collectAsState()
    viewModel.channel.collectInLaunchedEffect {
        when (it) {
            HomeContractChannel.NavigateToLanguageSelectPage -> onNavigateToLanguageSelectPage()
            HomeContractChannel.OpenDrawer -> openDrawer()
            is HomeContractChannel.ShowMessage -> showMessage(it.messageContent)
        }
    }

    SetStatusBarColor()
    HomeScreen(
        state = state,
        event = viewModel::event
    )
}

@Composable
private fun HomeScreen(
    state: HomeContractState,
    event: (HomeContractEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .baseBackground()
            .fillMaxSize()
    ) {
        val toolbarState = rememberToolbarState()
        val scrollState = rememberScrollState()
        ListenToScrollAndUpdateToolbarState(
            scrollState = scrollState,
            toolbarState = toolbarState
        )
        Crossfade(targetState = state) {
            when (it) {
                HomeContractState.Loading -> {
                    LoadingScreen(modifier = Modifier.fillMaxSize())
                }
                is HomeContractState.Success -> {
                    SuccessScreen(
                        state = it,
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .fillMaxSize(),
                    )
                }
                is HomeContractState.Empty -> {
                    EmptyScreen(modifier = Modifier.fillMaxSize())
                }
            }
        }
        HomeCollapsingToolbar(
            toolbarTitle = stringResource(id = R.string.HomePage_Toolbar_title),
            learningLanguageText = stringResource(R.string.HomePage_Toolbar_learningLanguage) + " " +
                stringResource(
                    state.learningLanguage?.languageNameResCode
                        ?: R.string.HomePage_Toolbar_learningLanguage
                ),
            welcomeText = stringResource(id = R.string.HomePage_Toolbar_welcome) + ", " +
                (state.userName ?: stringResource(id = R.string.HomePage_Toolbar_guest)),
            progress = toolbarState.progress,
            onClickDrawerIcon = { event(HomeContractEvent.OnClickDrawer) },
            onClickLearningLanguageIcon = { event(HomeContractEvent.OnClickToolbarLanguage) },
            modifier = Modifier
                .fillMaxWidth()
                .height(with(LocalDensity.current) { toolbarState.height.toDp() })
                .graphicsLayer { translationY = toolbarState.offset }
        )
    }
}

@Composable
private fun SuccessScreen(
    state: HomeContractState.Success,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = MaxToolbarHeight)
            .statusBarsPadding()
    ) {
        SetOfCardsItem(
            SetOfCardsUI(
                "sad", 12,
                true, null,
                null, null, "sdfsd"
            ),
            onClick = {},
            onClickFavorite = {
                println("hvb")
            }
        )
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
private fun rememberToolbarState(
    minHeight: Dp = MinToolbarHeight,
    maxHeight: Dp = MaxToolbarHeight
): ToolbarState {
    val minToolbarHeight = with(LocalDensity.current) {
        minHeight.roundToPx()
    }
    val maxToolbarHeight = with(LocalDensity.current) {
        maxHeight.roundToPx()
    }
    val toolbarHeightRange: IntRange = remember(maxToolbarHeight, minToolbarHeight) {
        minToolbarHeight..maxToolbarHeight
    }
    return rememberSaveable(saver = ExitUntilCollapsedState.Saver) {
        ExitUntilCollapsedState(toolbarHeightRange)
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
