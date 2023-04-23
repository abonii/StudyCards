package abm.co.permissions

import androidx.lifecycle.ViewModel


class PermissionViewModel : ViewModel() {

    var currentRequestCode: Int = 0
    private val rationalRequest = mutableMapOf<Int, Boolean>()

    fun updateRationalRequest(requestId: Int, value: Boolean) {
        rationalRequest[requestId] = value
    }

    fun removeRationalRequest(requestId: Int) {
        rationalRequest.remove(requestId)
    }

    fun getRationalRequest(requestId: Int): Boolean? {
        return rationalRequest[requestId]
    }
}
