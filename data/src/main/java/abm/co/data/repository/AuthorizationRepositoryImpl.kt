package abm.co.data.repository

import abm.co.data.model.DatabaseReferenceType.CURRENT_VERSION
import abm.co.data.model.DatabaseReferenceType.USER_REF
import abm.co.data.model.user.UserDTO
import abm.co.data.model.user.toDTO
import abm.co.domain.model.UserGoal
import abm.co.domain.model.UserInterest
import abm.co.domain.repository.AuthorizationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@ActivityRetainedScoped
class AuthorizationRepositoryImpl @Inject constructor(
    @Named(CURRENT_VERSION) private var rootDatabase: DatabaseReference,
    private val firebaseAuth: FirebaseAuth
) : AuthorizationRepository {

    private fun getUserID(): String = firebaseAuth.currentUser?.uid ?: "no-user-id"

    private fun getUserDatabase(): DatabaseReference {
        return rootDatabase.child(USER_REF).child(getUserID())
            .apply { keepSynced(true) }
    }

    override suspend fun setUserInfo(name: String?, email: String?, password: String?) {
        withContext(Dispatchers.IO) {
            getUserDatabase().updateChildren(
                mapOf(
                    UserDTO.name to name,
                    UserDTO.email to email,
                    UserDTO.password to password
                )
            )
        }
    }

    override suspend fun setUserGoal(userGoal: UserGoal) {
        withContext(Dispatchers.IO) {
            getUserDatabase().child(UserDTO.goals).setValue(userGoal)
        }
    }

    override suspend fun setUserInterests(
        userInterests: List<UserInterest>
    ) {
        withContext(Dispatchers.IO) {
            getUserDatabase().child(UserDTO.interests).setValue(userInterests.map { it.toDTO() })
        }
    }
}
