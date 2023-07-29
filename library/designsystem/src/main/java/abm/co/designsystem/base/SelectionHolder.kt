package abm.co.designsystem.base

data class SelectionHolder<Item>(
    val item: Item,
    val isSelected: Boolean = false
)
