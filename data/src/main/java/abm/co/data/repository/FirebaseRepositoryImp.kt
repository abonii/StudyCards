package abm.co.data.repository

import abm.co.data.di.ApplicationScope
import abm.co.data.model.DatabaseReferenceType.CONFIG_REF
import abm.co.data.model.DatabaseReferenceType.USER_REF
import abm.co.domain.repository.AuthorizationRepository
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.CoroutineScope

@ActivityRetainedScoped
class FirebaseRepositoryImp @Inject constructor(
    @ApplicationScope private val coroutineScope: CoroutineScope,
    @Named(USER_REF) private var userDbRef: DatabaseReference,
    @Named(CONFIG_REF) private var configKey: DatabaseReference,
    private var firebaseAuth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient,
) : AuthorizationRepository {

}
