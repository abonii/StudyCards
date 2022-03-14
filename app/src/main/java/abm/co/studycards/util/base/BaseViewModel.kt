package abm.co.studycards.util.base

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class BaseViewModel : ViewModel() {

    private val _toast = MutableSharedFlow<Any>()
    val toast = _toast.asSharedFlow()

    fun makeToast(any: Any) {
        launchIO {
            _toast.emit(any)
        }
    }

    fun makeToast(@StringRes id: Int) {
        launchIO {
            _toast.emit(id)
        }
    }

    // Do work in IO
    fun <P> launchIO(doOnAsyncBlock: suspend CoroutineScope.() -> P): Job {
        return viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
        }) {
            withContext(Dispatchers.IO) {
                doOnAsyncBlock.invoke(this)
            }
        }
    }

}