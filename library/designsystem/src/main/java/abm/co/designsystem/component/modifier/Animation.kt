package abm.co.designsystem.component.modifier

import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp


/**
 * Creates an animation of type [Dp] that runs infinitely as a part of the given
 * [InfiniteTransition].
 *
 * Once the animation is created, it will run from [initialValue] to [targetValue] and repeat.
 * Depending on the [RepeatMode] of the provided [animationSpec], the animation could either
 * restart after each iteration (i.e. [RepeatMode.Restart]), or reverse after each iteration (i.e
 * . [RepeatMode.Reverse]).
 *
 * If [initialValue] or [targetValue] is changed at any point during the animation, the animation
 * will be restarted with the new [initialValue] and [targetValue]. __Note__: this means
 * continuity will *not* be preserved.
 *
 * @sample androidx.compose.animation.core.samples.InfiniteTransitionAnimateValueSample
 *
 * @see [InfiniteTransition.animateFloat]
 * @see [androidx.compose.animation.animateColor]
 */
@Composable
fun InfiniteTransition.animateDp(
    initialValue: Dp,
    targetValue: Dp,
    animationSpec: InfiniteRepeatableSpec<Dp>
): State<Dp> = animateValue(initialValue, targetValue, Dp.VectorConverter, animationSpec)
