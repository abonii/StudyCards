package abm.co.studycards.ui.forgot_password

import abm.co.studycards.R
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.core.App
import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val firebaseAuthInstance: FirebaseAuth
) : BaseViewModel() {

    var email: String = ""

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun sendResetPassword() = viewModelScope.launch {
        if (TextUtils.isEmpty(email)) {
            makeToast(App.instance.getString(R.string.email_empty))
            return@launch
        }
        _loading.value = true
        firebaseAuthInstance.sendPasswordResetEmail(email.trim())
            .addOnCompleteListener {
                _loading.value = false
                if (it.isSuccessful) {
                    makeToast(App.instance.getString(R.string.we_send_reset_password))
                } else {
                    makeToast(App.instance.getString(R.string.we_couldnt_send_verification))
                }
            }
    }
}