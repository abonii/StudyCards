package abm.co.designsystem.extensions

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch

fun <T> LazyListScope.gridItemsIndexed(
    data: List<T>,
    spanCount: Int,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    key: ((index: Int, item: T) -> Any)? = null,
    itemModifier: (columnIndex: Int) -> Modifier = { Modifier },
    contentType: (index: Int) -> Any? = { null },
    itemContent: @Composable BoxScope.(index: Int, item: T) -> Unit
) {
    gridItemsIndexed(
        data = data,
        spanCount = spanCount,
        horizontalArrangement = horizontalArrangement,
        key = key,
        itemModifier = itemModifier,
        contentType = contentType,
    ) { index, item, _, _ ->
        itemContent.invoke(this, index, item)
    }
}

fun <T> LazyListScope.gridItemsIndexed(
    data: List<T>,
    spanCount: Int,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    key: ((index: Int, item: T) -> Any)? = null,
    itemModifier: (columnIndex: Int) -> Modifier = { Modifier },
    contentType: (index: Int) -> Any? = { null },
    itemContent: @Composable (BoxScope.(index: Int, item: T, rawIndex: Int, columnIndex: Int) -> Unit)
) {
    val rows = if (data.isEmpty()) 0 else 1 + (data.count() - 1) / spanCount
    items(count = rows, contentType = contentType) { columnIndex ->
        Row(horizontalArrangement = horizontalArrangement) {
            for (rowIndex in 0 until spanCount) {
                val itemIndex = columnIndex * spanCount + rowIndex
                if (itemIndex < data.count()) {
                    val item = data[itemIndex]
                    key(key?.invoke(itemIndex, item)) {
                        Box(
                            modifier = itemModifier(columnIndex),
                            propagateMinConstraints = true
                        ) {
                            itemContent.invoke(
                                this,
                                itemIndex,
                                item,
                                rowIndex,
                                columnIndex
                            )
                        }
                    }
                } else {
                    Spacer(Modifier.weight(weight = 1f))
                }
            }
        }
    }
}

@Composable
fun <T> GridItems(
    data: List<T>,
    spanCount: Int,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    key: ((index: Int, item: T) -> Any)? = null,
    itemModifier: (columnIndex: Int) -> Modifier = { Modifier },
    itemContent: @Composable BoxScope.(index: Int, item: T, rawIndex: Int, columnIndex: Int) -> Unit
) {
    val rows = if (data.isEmpty()) 0 else 1 + (data.count() - 1) / spanCount
    (0 until rows).forEach {columnIndex ->
        Row(horizontalArrangement = horizontalArrangement) {
            for (rowIndex in 0 until spanCount) {
                val itemIndex = columnIndex * spanCount + rowIndex
                if (itemIndex < data.count()) {
                    val item = data[itemIndex]
                    key(key?.invoke(itemIndex, item)) {
                        Box(
                            modifier = itemModifier(columnIndex),
                            propagateMinConstraints = true
                        ) {
                            itemContent.invoke(
                                this,
                                itemIndex,
                                item,
                                rowIndex,
                                columnIndex
                            )
                        }
                    }
                } else {
                    Spacer(Modifier.weight(weight = 1f))
                }
            }
        }
    }
}

class VerticalArrangementLastItem : Arrangement.Vertical {
    override fun Density.arrange(
        totalSize: Int,
        sizes: IntArray,
        outPositions: IntArray
    ) {
        var currentOffset = 0
        sizes.forEachIndexed { index, size ->
            if (index == sizes.lastIndex) {
                outPositions[index] = totalSize - size
            } else {
                outPositions[index] = currentOffset
                currentOffset += size
            }
        }
    }
}

fun LazyListState.disableScrolling(scope: CoroutineScope) {
    scope.launch {
        scroll(scrollPriority = MutatePriority.PreventUserInput) {
            // Await indefinitely, blocking scrolls
            awaitCancellation()
        }
    }
}

fun LazyListState.enableScrolling(scope: CoroutineScope) {
    scope.launch {
        scroll(scrollPriority = MutatePriority.PreventUserInput) {
            // Do nothing, just cancel the previous indefinite "scroll"
        }
    }
}
