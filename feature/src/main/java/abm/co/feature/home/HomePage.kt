package abm.co.feature.home

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.animateDp
import abm.co.designsystem.component.modifier.baseBackground
import abm.co.designsystem.component.systembar.SetStatusBarColor
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.home.component.HomeCollapsingToolbar
import abm.co.feature.toolbar.ToolbarState
import abm.co.feature.toolbar.scrollflags.ExitUntilCollapsedState
import abm.co.feature.utils.AnalyticsManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

private val MinToolbarHeight = 90.dp
private val MaxToolbarHeight = 174.dp

@Composable
fun HomePage(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        AnalyticsManager.sendEvent("home_page_viewed")
    }
    val state by viewModel.state.collectAsState()

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
        val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val minToolbarHeight = with(LocalDensity.current) {
            MinToolbarHeight.roundToPx()
        }
        val maxToolbarHeight = with(LocalDensity.current) {
            MaxToolbarHeight.roundToPx()
        }
        val toolbarHeightRange: IntRange = remember(maxToolbarHeight, minToolbarHeight) {
            minToolbarHeight..maxToolbarHeight
        }

        val toolbarState = rememberToolbarState(toolbarHeightRange)
        val scrollState = rememberScrollState()

        toolbarState.scrollValue = scrollState.value
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PaddingValues(top = MaxToolbarHeight + statusBarHeight).calculateTopPadding())
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = 36.dp)
                    .height(500.dp)
                    .fillMaxWidth(),
                text = "Оh, we don't have the words to study yet.\n" +
                    "Let's add new words to the add button",
                style = StudyCardsTheme.typography.weight400Size14LineHeight24,
                color = StudyCardsTheme.colors.middleGray,
                textAlign = TextAlign.Center
            )
            AnimatableArrow(
                visible = true, // todo
                modifier = Modifier.height(500.dp).align(CenterHorizontally)
            )
        }

        HomeCollapsingToolbar(
            backgroundImageResId = abm.co.designsystem.R.drawable.image_finished,
            progress = toolbarState.progress,
            minToolbarHeight = minToolbarHeight,
            maxToolbarHeight = maxToolbarHeight,
            modifier = Modifier
                .fillMaxWidth()
                .height(with(LocalDensity.current) { toolbarState.height.toDp() })
                .graphicsLayer { translationY = toolbarState.offset }
        )
    }
}

@Composable
private fun AnimatableArrow(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible
    ) {
        BoxWithConstraints {
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
}

@Composable
fun rememberToolbarState(toolbarHeightRange: IntRange): ToolbarState {
    return rememberSaveable(saver = ExitUntilCollapsedState.Saver) {
        ExitUntilCollapsedState(toolbarHeightRange)
    }
}