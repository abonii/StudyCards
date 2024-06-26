package abm.co.data.repository

import abm.co.data.model.DatabaseRef
import abm.co.data.model.DatabaseRef.ROOT_REF
import abm.co.data.model.DatabaseRef.USER_REF
import abm.co.data.model.user.toDTO
import abm.co.domain.model.User
import abm.co.domain.model.UserGoal
import abm.co.domain.model.UserInterest
import abm.co.domain.repository.AuthorizationRepository
import abm.co.domain.repository.ConfigRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

@ActivityRetainedScoped
class AuthorizationRepositoryImpl @Inject constructor(
    @Named(ROOT_REF) private var rootDatabase: DatabaseReference,
    private val firebaseAuth: FirebaseAuth,
    private val configRepository: ConfigRepository
) : AuthorizationRepository {

    private fun getUserID(): String = firebaseAuth.currentUser?.uid ?: "no-user-id"

    private fun getUserDatabase(): DatabaseReference {
        return rootDatabase.child(USER_REF).child(getUserID())
            .child(DatabaseRef.USER_PROPERTIES_REF)
    }

    override suspend fun setUserInfo(name: String?, email: String?, password: String?) {
        withContext(Dispatchers.IO) {
            val userDatabase = getUserDatabase()
            name?.let { userDatabase.child(User.name).setValue(it) }
            email?.let { userDatabase.child(User.email).setValue(it) }
            password?.let { userDatabase.child(User.password).setValue(it) }
        }
    }

    override suspend fun updateUserTranslationCount(count: Long) {
        withContext(Dispatchers.IO) {
            val userDatabase = getUserDatabase()
            count.let { userDatabase.child(User.translateCounts).setValue(it) }
        }
    }

    override suspend fun addUserTranslationCount() {
        withContext(Dispatchers.IO) {
            val config = configRepository.getConfig().firstOrNull()?.asRight?.b
            val userDatabase = getUserDatabase()
            config?.translateCount.let {
                userDatabase.child(User.translateCounts).setValue(it)
            }
        }
    }

    override suspend fun setUserGoal(userGoal: UserGoal) {
        withContext(Dispatchers.IO) {
            getUserDatabase().child(User.goal).setValue(userGoal.toDTO())
        }
    }

    override suspend fun setUserInterests(
        userInterests: List<UserInterest>
    ) {
        withContext(Dispatchers.IO) {
            getUserDatabase().child(User.interests).setValue(userInterests.map { it.toDTO() })
        }
    }
}
