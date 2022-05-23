package abm.co.studycards.ui.profile

import abm.co.studycards.R
import abm.co.studycards.domain.Prefs
import abm.co.studycards.domain.model.Language
import abm.co.studycards.domain.repository.ServerCloudRepository
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.firebaseError
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val prefs: Prefs,
    val googleSignInClient: GoogleSignInClient,
    private val firebaseRepository: ServerCloudRepository,
) : BaseViewModel() {

    private val currentUser = firebaseRepository.getFirebaseAuth().currentUser
    val appLanguage = prefs.getAppLanguage()
    var email = currentUser?.email ?: ""
        set(value) {
            field = value
            _emailError.value = null
        }
    var password: String = ""
        set(value) {
            field = value
            _passwordError.value = null
        }
    var emailDisplay = MutableStateFlow(currentUser?.email ?: "")
    val userName = MutableStateFlow(currentUser?.displayName ?: "")
    val userPhotoUrl = MutableStateFlow(currentUser?.photoUrl)

    private val _emailError = MutableStateFlow<Int?>(null)
    val emailError = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<Int?>(null)
    val passwordError = _passwordError.asStateFlow()

    val isAnonymous = MutableStateFlow(currentUser?.isAnonymous == true)

    val isVerified = MutableStateFlow(currentUser?.isEmailVerified == true)

    val isEmailAuth = MutableStateFlow(currentUser?.isEmailVerified == true)

    val isAnonymousOrVerified =
        MutableStateFlow(
            currentUser?.isAnonymous == true || currentUser?.isEmailVerified == true
        )

    val translationCount = MutableStateFlow("0")

    init {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseAuth.getInstance().useAppLanguage()
            firebaseRepository.fetchUserInfo().collectLatest {
                userName.value = it.name
                translationCount.value = it.translateCounts.toString()
            }
        }
    }

    fun setAppLanguage(language: Language) {
        prefs.setAppLanguage(language.code)
    }

    private fun createUserFromAnonymous(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        currentUser?.linkWithCredential(credential)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    sendVerificationEmail()
                    updateUser()
                } else {
                    makeToast(firebaseError(it.exception))
                }
            }
    }

    private fun updateUser() {
        viewModelScope.launch(Dispatchers.IO) {
            if (currentUser != null) {
                currentUser.reload()
                userName.value = currentUser.displayName ?: ""
                emailDisplay.value = currentUser.email ?: ""
                userPhotoUrl.value = currentUser.photoUrl
                isAnonymous.value = currentUser.isAnonymous
                isVerified.value = currentUser.isEmailVerified
                isAnonymousOrVerified.value =
                    isAnonymous.value == true || isVerified.value == true
            }
        }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        currentUser?.linkWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateUser()
                } else {
                    makeToast(firebaseError(task.exception))
                }
            }
    }

    fun onClickRegistration() {
        val email = this.email.trim()
        val password = this.password.trim()
        if (email.isBlank()) {
            _emailError.value = R.string.email_empty
        } else {
            _emailError.value = null
            when {
                password.isEmpty() -> {
                    _passwordError.value = R.string.password_empty
                }
                password.length <= 5 -> {
                    _passwordError.value = R.string.password_length
                }
                else -> {
                    _passwordError.value = null
                    createUserFromAnonymous(email, password)
                }
            }
        }
    }

    fun removeDatabaseOfUser(onFinish: () -> Unit) {
        viewModelScope.launch {
            firebaseRepository.removeUser()
            signOut(onFinish)
        }
    }

    private fun signOut(onFinish: () -> Unit) {
        firebaseRepository.getFirebaseAuth().signOut()
        googleSignInClient.signOut()
            .addOnCompleteListener {
                currentUser?.delete()
                onFinish()
            }
    }

    fun sendVerificationEmail() {
        currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    makeToast(R.string.we_send_verification)
                } else {
                    makeToast(firebaseError(task.exception))
                }
            }
    }

    fun simpleLogout(onFinish: () -> Unit) {
        firebaseRepository.getFirebaseAuth().signOut()
        googleSignInClient.signOut()
            .addOnCompleteListener {
                onFinish.invoke()
            }
    }
}