package abm.co.data.repository

import abm.co.data.di.ApplicationScope
import abm.co.data.model.DatabaseReferenceType.CONFIG_REF
import abm.co.data.model.DatabaseReferenceType.USER_REF
import abm.co.data.model.user.UserDTO
import abm.co.data.model.user.toDomain
import abm.co.data.utils.asFlow
import abm.co.domain.base.Either
import abm.co.domain.base.Failure
import abm.co.domain.model.User
import abm.co.domain.repository.ServerRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

@ActivityRetainedScoped
class FirebaseRepositoryImpl @Inject constructor(
    @Named(USER_REF) private var userDatabase: DatabaseReference,
    @Named(CONFIG_REF) private var config: DatabaseReference,
    @ApplicationScope private val coroutineScope: CoroutineScope,
    private val gson: Gson
) : ServerRepository {

    override suspend fun getUser(): Flow<Either<Failure, User?>> {
        return userDatabase.asFlow(scope = coroutineScope, converter = { snapshot ->
            val map = snapshot.getValue(object: GenericTypeIndicator<Map<String, Any>?>(){})
            val json = gson.toJson(map)
            val userDTO = gson.fromJson(json, UserDTO::class.java)
            userDTO?.toDomain()
        })
    }

    companion object {
        val className: String = this::class.java.simpleName
    }
}
