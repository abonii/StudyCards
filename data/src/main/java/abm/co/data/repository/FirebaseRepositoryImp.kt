package abm.co.data.repository

import abm.co.data.model.DatabaseReferenceType.CATEGORIES_REF
import abm.co.data.model.DatabaseReferenceType.CONFIG_REF
import abm.co.data.model.DatabaseReferenceType.EXPLORE_REF
import abm.co.data.model.DatabaseReferenceType.USER_REF
import abm.co.domain.repository.ServerCloudRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.CoroutineScope

@ActivityRetainedScoped
class FirebaseRepositoryImp @Inject constructor(
    @Named(EXPLORE_REF) private val exploreDbRef: DatabaseReference,
    @Named(CATEGORIES_REF) private val categoriesDbRef: DatabaseReference,
    @Named(USER_REF) private var userDbRef: DatabaseReference,
    @Named(CONFIG_REF) private var configKey: DatabaseReference,
    private var _firebaseAuth: FirebaseAuth,
    private val coroutineScope: CoroutineScope,
) : ServerCloudRepository {

}