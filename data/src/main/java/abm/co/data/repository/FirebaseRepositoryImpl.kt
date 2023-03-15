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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

@ActivityRetainedScoped
class FirebaseRepositoryImpl @Inject constructor(
    @Named(USER_REF) private var userDatabase: DatabaseReference,
    @Named(CONFIG_REF) private var config: DatabaseReference,
    @ApplicationScope private val coroutineScope: CoroutineScope
) : ServerRepository {

    override suspend fun getUser(): Flow<Either<Failure, User?>> {
        return userDatabase.asFlow(scope = coroutineScope, converter = { snapshot ->
            snapshot.getValue<UserDTO>()?.toDomain()
        })
    }

    companion object {
        val className: String = this::class.java.simpleName
    }
}
