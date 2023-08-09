package abm.co.designsystem.message.snackbar

import abm.co.designsystem.R
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.message.common.MessageSnackbarContent
import abm.co.designsystem.theme.StudyCardsTheme
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@ExperimentalSerializationApi
suspend fun SnackbarHostState.showSnackbarWithContent(
    messageSnackbarContent: MessageSnackbarContent
) {
    val json = Json.encodeToString(messageSnackbarContent)
    showSnackbar(message = json)
}

@ExperimentalSerializationApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MessageSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    if (snackbarHostState.currentSnackbarData != null) {
        var size by remember { mutableStateOf(Size.Zero) }
        val swipeableState = rememberSwipeableState(SwipeDirection.Initial)
        val height = remember {
            derivedStateOf {
                if (size.height == 0f) {
                    1f
                } else {
                    size.height
                }
            }
        }
        if (swipeableState.isAnimationRunning) {
            DisposableEffect(Unit) {
                onDispose {
                    when (swipeableState.currentValue) {
                        SwipeDirection.Up -> snackbarHostState.currentSnackbarData?.dismiss()
                        else -> return@onDispose
                    }
                }
            }
        }
        val density = LocalDensity.current
        val offset = remember(swipeableState) {
            derivedStateOf {
                with(density) {
                    swipeableState.offset.value.toDp()
                }
            }
        }
        val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        MessageSnackbarHost(
            hostState = snackbarHostState,
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(Alignment.Top)
                .onSizeChanged { size = Size(it.width.toFloat(), it.height.toFloat()) }
                .swipeable(
                    state = swipeableState,
                    anchors = mapOf(
                        -height.value to SwipeDirection.Up,
                        0f to SwipeDirection.Initial
                    ),
                    thresholds = { _, _ -> FractionalThreshold(0.2f) },
                    orientation = Orientation.Vertical
                )
                .padding(start = 16.dp, top = statusBarHeight * 1.5f, end = 16.dp),
            snackbar = { data ->
                val messageSnackbarContent = remember(data) {
                    Json.decodeFromString<MessageSnackbarContent>(data.message)
                }
                MessageSnackbarView(
                    messageSnackbarContent = messageSnackbarContent,
                    cornerShape = RoundedCornerShape(12.dp),
                    verticalOffset = offset.value
                )
            }
        )
    }
}

@Composable
private fun MessageSnackbarView(
    messageSnackbarContent: MessageSnackbarContent,
    cornerShape: Shape,
    verticalOffset: Dp
) {
    val haptic = LocalHapticFeedback.current
    LaunchedEffect(messageSnackbarContent.type) {
        when (messageSnackbarContent.type) {
            MessageType.Info -> {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }

            MessageType.Success -> {
                repeat(2) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    delay(100)
                }
            }

            MessageType.Error -> {
                repeat(4) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    delay(100)
                }
            }
        }
    }

    Card(
        backgroundColor = colorResource(id = R.color.fill_background_primary),
        shape = cornerShape,
        border = BorderStroke(
            width = 0.5.dp,
            color = colorResource(id = R.color.fill_stroke)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .offset {
                IntOffset(
                    y = verticalOffset.roundToPx(),
                    x = 0
                )
            }
            .graphicsLayer {
                shadowElevation = 17.dp.toPx()
            }
    ) {
        Column {
            Text(
                modifier = Modifier.padding(start = 14.dp, top = 12.dp, end = 10.dp),
                text = messageSnackbarContent.title,
                color = when (messageSnackbarContent.type) {
                    MessageType.Success -> StudyCardsTheme.colors.success
                    MessageType.Error -> StudyCardsTheme.colors.error
                    MessageType.Info -> StudyCardsTheme.colors.textPrimary
                },
                style = StudyCardsTheme.typography.weight600Size14LineHeight18
            )
            Text(
                modifier = Modifier.padding(
                    start = 14.dp,
                    top = 4.dp,
                    end = 18.dp,
                    bottom = 12.dp
                ),
                text = messageSnackbarContent.subtitle,
                color = StudyCardsTheme.colors.textPrimary,
                style = StudyCardsTheme.typography.weight400Size14LineHeight18
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MessageSnackbarViewPreview() {
    val cornerRadius = remember { 12.dp }
    val cornerShape = remember { RoundedCornerShape(cornerRadius) }
    val messageSnackbarContent = remember {
        MessageSnackbarContent(
            title = "Error",
            subtitle = "real error",
            type = MessageType.Error
        )
    }
    MessageSnackbarView(
        messageSnackbarContent = messageSnackbarContent,
        cornerShape = cornerShape,
        verticalOffset = 0.dp
    )
}
