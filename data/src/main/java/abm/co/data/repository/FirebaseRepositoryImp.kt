package abm.co.data.repository

import abm.co.data.di.ApplicationScope
import abm.co.data.model.DatabaseReferenceType.CONFIG_REF
import abm.co.data.model.DatabaseReferenceType.USER_REF
import abm.co.domain.base.Either
import abm.co.domain.base.Failure
import abm.co.domain.repository.ServerRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope

@Singleton
class FirebaseRepositoryImp @Inject constructor(
    @ApplicationScope private val coroutineScope: CoroutineScope,
    @Named(USER_REF) private var userDbRef: DatabaseReference,
    @Named(CONFIG_REF) private var configKey: DatabaseReference,
) : ServerRepository {

    override suspend fun setUserInfoAfterSignUp(
        email: String?,
        password: String?
    ): Either<Failure, Unit> {
        println("email: $email - password: $password")
        return Either.Empty
    }

}
