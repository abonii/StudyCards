package abm.co.studycards.util.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow

abstract class BaseViewModel : ViewModel() {

    private val _toast = MutableSharedFlow<String>()
    val toast = MutableSharedFlow<String>()

    fun makeToast(string: String){
        launchIO {
            _toast.emit(string)
        }
    }

    // Do work in IO
    fun <P> launchIO(doOnAsyncBlock: suspend CoroutineScope.() -> P): Job {
        return viewModelScope.launch(CoroutineExceptionHandler { _, e ->
        }) {
            withContext(Dispatchers.IO) {
                doOnAsyncBlock.invoke(this)
            }
        }
    }

}