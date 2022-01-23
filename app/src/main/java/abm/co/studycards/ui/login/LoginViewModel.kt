package abm.co.studycards.ui.login

import abm.co.studycards.util.base.BaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val googleSignInClient: GoogleSignInClient,
    val firebaseAuthInstance: FirebaseAuth,
) : BaseViewModel()