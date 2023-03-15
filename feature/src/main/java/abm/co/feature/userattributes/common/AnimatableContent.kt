package abm.co.feature.userattributes.common

import abm.co.designsystem.component.modifier.Modifier
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AnimatableContent(
    visible: Boolean,
    isToRight: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val enterAnimationToRight = slideInHorizontally { it / 2 } + fadeIn()
    val exitAnimationToRight = slideOutHorizontally { -it } + fadeOut()
    val enterAnimationToLeft = slideInHorizontally { -it / 2 } + fadeIn()
    val exitAnimationToLeft = slideOutHorizontally { it } + fadeOut()

    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = if (isToRight) enterAnimationToRight else enterAnimationToLeft,
        exit = if (isToRight) exitAnimationToRight else exitAnimationToLeft
    ) {
        content()
    }
}