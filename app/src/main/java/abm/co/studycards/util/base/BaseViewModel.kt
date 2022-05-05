package abm.co.studycards.util.base

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    private val _toast = MutableSharedFlow<Any>()
    val toast = _toast.asSharedFlow()
    fun makeToast(any: Any) {
        viewModelScope.launch {
            _toast.emit(any)
        }
    }

    fun makeToast(@StringRes id: Int) {
        viewModelScope.launch {
            _toast.emit(id)
        }
    }
}