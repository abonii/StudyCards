package abm.co.studycards.ui.profile

import abm.co.studycards.data.model.Language
import abm.co.studycards.data.pref.Prefs
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val prefs: Prefs,
    val googleSignInClient: GoogleSignInClient,
    val firebaseAuthInstance: FirebaseAuth
) : ViewModel() {

    fun isAnonymous() =
        firebaseAuthInstance.currentUser?.isAnonymous == true
    val appLanguage = prefs.getAppLanguage()
//    val targetLang = prefs.getTargetLanguage()

    fun setAppLanguage(language: Language) {
        val lang = prefs.getAppLanguage()
        if (lang != language.code) {
            prefs.setAppLanguage(language.code)
        }
    }

}