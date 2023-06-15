package abm.co.designsystem.component.dialog

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NonDraggableBottomDialogView(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    scrimColor: Color = Color(0xFF161618).copy(0.7f),
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .background(scrimColor)
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .animateEnterExit(
                        enter = slideInVertically(
                            animationSpec = tween(200),
                            initialOffsetY = {
                                constraints.maxHeight
                            }
                        ),
                        exit = slideOutVertically(
                            animationSpec = tween(200),
                            targetOffsetY = {
                                constraints.maxHeight
                            }
                        )
                    )
                    .disableClickAndRipple(),
                content = {
                    content()
                }
            )
        }
    }

    if (visible) {
        BackHandler {
            onDismiss()
        }
    }
}

private fun Modifier.disableClickAndRipple(): Modifier = composed {
    clickable(
        enabled = false,
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = { }
    )
}
