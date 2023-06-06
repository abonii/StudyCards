package abm.co.feature.book.reader

import abm.co.designsystem.base.BaseFragment
import abm.co.designsystem.base.messageContent
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.book.reader.component.BookPage
import android.view.View
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.sp
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class BookReaderFragment : BaseFragment() {

    companion object {
        private val rootViewId = View.generateViewId()
    }

    override val rootViewId: Int get() = Companion.rootViewId

    @Composable
    override fun InitUI(messageContent: messageContent) {
        BoxWithConstraints {
            val density = LocalDensity.current
            val style = StudyCardsTheme.typography.weight500Size16LineHeight20
                .copy(letterSpacing = 0.sp)
            println("Box Size: $maxHeight - $maxWidth")
            println("Style: $style")
            Text(
                text = "1234567890".repeat(1000),
                style = style
            )
            Text(
                modifier = Modifier
                    .onGloballyPositioned {
                        with(density) {
                            println("long: ${it.size.width.toDp()}")
                        }
                    },
                text = "1234567890",
                style = style
            )
            Text(
                modifier = Modifier
                    .onGloballyPositioned {
                        with(density) {
                            println("short: ${it.size.width.toDp()}")
                        }
                    },
                text = "9",
                style = style
            )
        }
    }


    @Composable
    private fun MeasureUnconstrainedViewHeight(
        text: String,
        fontStyle: TextStyle,
        modifier: Modifier = Modifier,
        content: @Composable (List<String>) -> Unit
    ) {
        SubcomposeLayout(modifier = modifier) { constraints ->
            val viewToMeasurePlaceable = subcompose(
                slotId = "viewToMeasure",
                content = viewToMeasure
            )[0].measure(constraints = Constraints(maxWidth = constraints.maxWidth))

            val maxHeight = abs(constraints.maxHeight - viewToMeasurePlaceable.height)
            val maxWidth = constraints.maxWidth

            val contentPlaceable = subcompose(
                slotId = "content",
                content = {
                    content(maxHeight, maxWidth)
                }
            ).getOrNull(0)?.measure(constraints = constraints)
            layout(contentPlaceable?.width ?: 0, contentPlaceable?.height ?: 0) {
                contentPlaceable?.place(0, 0)
            }
        }
    }
}
