package abm.co.studycards.ui.profile

import abm.co.studycards.R
import abm.co.studycards.data.model.Language
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.util.Constants.CAN_TRANSLATE_TIME_EVERY_DAY
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.core.App
import abm.co.studycards.util.firebaseError
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val prefs: Prefs,
    val googleSignInClient: GoogleSignInClient,
    private val firebaseRepository: ServerCloudRepository,
) : BaseViewModel() {

    private val currentUser = FirebaseAuth.getInstance().currentUser
    val appLanguage = prefs.getAppLanguage()
    var email = MutableStateFlow(currentUser?.email ?: "")
    var password: String = ""
    var emailDisplay = MutableStateFlow(currentUser?.email ?: "")
    val userName = MutableStateFlow(currentUser?.displayName ?: "")
    val userPhotoUrl = MutableStateFlow(currentUser?.photoUrl)

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError = _passwordError.asStateFlow()

    val isAnonymous = MutableStateFlow(currentUser?.isAnonymous == true)

    val isVerified = MutableStateFlow(currentUser?.isEmailVerified == true)

    val isAnonymousButVerified =
        MutableStateFlow(currentUser?.isAnonymous == true || currentUser?.isEmailVerified == true)

    val translationCount = MutableStateFlow("0")

    init {
        FirebaseAuth.getInstance().useAppLanguage()
        firebaseRepository.getUserReference().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch(Dispatchers.IO) {
                    if (snapshot.child("name").exists()) {
                        userName.value = (snapshot.child("name").value as String?).toString()
                    }
                    translationCount.value =
                        (snapshot.child(CAN_TRANSLATE_TIME_EVERY_DAY).value as Long?).toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                translationCount.value = "0"
            }
        })
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
                userName.value = currentUser.displayName ?: ""
                emailDisplay.value = currentUser.email ?: ""
                userPhotoUrl.value = currentUser.photoUrl
                isAnonymous.value = currentUser.isAnonymous
                isVerified.value = currentUser.isEmailVerified
                isAnonymousButVerified.value =
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
        val email = this.email.value.trim()
        val password = this.password.trim()
        if (email.isBlank()) {
            _emailError.value = App.instance.getString(R.string.email_empty)
        } else {
            _emailError.value = null
            when {
                password.isEmpty() -> {
                    _passwordError.value = App.instance.getString(R.string.password_empty)
                }
                password.length <= 5 -> {
                    _passwordError.value = App.instance.getString(R.string.password_length)
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
            firebaseRepository.getUserReference().removeValue()
            signOut(onFinish)
        }
    }

    private fun signOut(onFinish: () -> Unit) {
        Firebase.auth.signOut()
        googleSignInClient.signOut()
            .addOnCompleteListener() {
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
}