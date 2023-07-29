package abm.co.designsystem.base

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

data class SelectionStateHolder<Item>(
    val item: Item,
    val isSelected: Boolean = false
) {
    private val _selectedState = mutableStateOf(isSelected)
    val selectedState: State<Boolean> = _selectedState

    fun setSelectedValue(selected: Boolean) {
        _selectedState.value = selected
    }
}
