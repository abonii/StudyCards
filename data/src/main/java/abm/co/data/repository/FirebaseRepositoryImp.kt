package abm.co.data.repository

import abm.co.data.di.ApplicationScope
import abm.co.data.model.DatabaseReferenceType.CONFIG_REF
import abm.co.data.model.DatabaseReferenceType.USER_REF
import abm.co.data.model.user.UserDTO
import abm.co.data.model.user.toDTO
import abm.co.data.model.user.toDomain
import abm.co.domain.model.User
import abm.co.domain.model.UserGoal
import abm.co.domain.model.UserInterest
import abm.co.domain.repository.ServerRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.mocklets.pluto.PlutoLog
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

@Singleton
class FirebaseRepositoryImp @Inject constructor(
    @Named(USER_REF) private var userDatabase: DatabaseReference,
    @Named(CONFIG_REF) private var config: DatabaseReference,
    @ApplicationScope private val coroutineScope: CoroutineScope
) : ServerRepository {

    override suspend fun setUserInfoAfterSignUp(
        email: String?, password: String?
    ) {
        userDatabase.setValue(mapOf(UserDTO.email to email, UserDTO.password to password))
    }

    override suspend fun setUserGoal(userGoal: UserGoal) {
        userDatabase.setValue(UserDTO.goals, userGoal.toDTO())
    }

    override suspend fun setUserInterests(
        userInterests: List<UserInterest>
    ) {
        userDatabase.setValue(UserDTO.interests, userInterests.map { it.toDTO() })
    }

    private val userStateFlow = MutableStateFlow<User?>(null)
    override suspend fun getUser(): Flow<User> {
        if (userStateFlow.value != null) return userStateFlow.filterNotNull()
        userDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    snapshot.getValue<UserDTO>()?.toDomain()?.let {
                        userStateFlow.value = it
                    }
                } catch (e: DatabaseException) {
                    PlutoLog.e("$className.getUser.catch", "${e.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                PlutoLog.e("$className.getUser.cancel", error.message)
            }
        })
        return userStateFlow.filterNotNull()
    }

    companion object {
        val className: String = this::class.java.simpleName
    }
}
