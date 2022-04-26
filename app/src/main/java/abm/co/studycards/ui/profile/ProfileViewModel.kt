package abm.co.studycards.ui.profile

import abm.co.studycards.R
import abm.co.studycards.data.model.Language
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.util.Constants
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.core.App
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val prefs: Prefs,
    val googleSignInClient: GoogleSignInClient,
    val firebaseAuthInstance: FirebaseAuth,
    @Named(Constants.USERS_REF) val userDbRef: DatabaseReference,
) : BaseViewModel() {

    fun isAnonymous() =
        firebaseAuthInstance.currentUser?.isAnonymous == true

    fun isVerified() = firebaseAuthInstance.currentUser?.isEmailVerified == true

    val translationCount = MutableStateFlow("0")

    init {
        userDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch {
                    translationCount.value =
                        (snapshot.child("canTranslateTimeEveryDay").value as Long).toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                translationCount.value = "0"
            }
        })
    }

            val appLanguage = prefs.getAppLanguage()

            var email = firebaseAuthInstance.currentUser?.email
            var password: String = ""
            val userName = firebaseAuthInstance.currentUser?.displayName
            val userPhotoUrl = firebaseAuthInstance.currentUser?.photoUrl

            private val _emailError = MutableStateFlow<String?>(null)
            val emailError = _emailError.asStateFlow()

            private val _passwordError = MutableStateFlow<String?>(null)
            val passwordError = _passwordError.asStateFlow()


            fun setAppLanguage(language: Language) {
                prefs.setAppLanguage(language.code)
            }

            private fun createUserFromAnonymous(email: String, password: String) {
                val credential = EmailAuthProvider.getCredential(email, password)
                firebaseAuthInstance.currentUser?.linkWithCredential(credential)
                    ?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            sendVerificationEmail()
                        } else {
                            makeToast("${it.exception?.message}")
                        }
                    }
            }

            private fun sendVerificationEmail() {
                firebaseAuthInstance.currentUser?.sendEmailVerification()
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            makeToast(R.string.we_send_verification)
                        } else {
                            makeToast(R.string.we_couldnt_send_verification)
                        }
                    }
            }

            fun firebaseAuthWithGoogle(idToken: String) {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                firebaseAuthInstance.currentUser?.linkWithCredential(credential)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            makeToast(R.string.linked_successfully)
                        } else {
                            makeToast(task.exception?.message.toString())
                        }
                    }
            }

            fun onClickRegistration() {
                val email = this.email?.trim()
                val password = this.password.trim()
                when {
                    email.isNullOrBlank() -> {
                        _emailError.value = App.instance.getString(R.string.email_empty)
                    }
                    password.isEmpty() -> {
                        _passwordError.value = App.instance.getString(R.string.password_empty)
                    }
                    password.length < 5 -> {
                        _passwordError.value = App.instance.getString(R.string.password_empty)
                    }
                    else -> {
                        createUserFromAnonymous(email, password)
                    }
                }
            }

        }