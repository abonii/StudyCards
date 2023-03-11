package abm.co.data.repository

import abm.co.data.model.DatabaseReferenceType.CONFIG_REF
import abm.co.data.model.DatabaseReferenceType.USER_REF
import abm.co.domain.base.Either
import abm.co.domain.base.Failure
import abm.co.domain.model.User
import abm.co.domain.model.UserGoal
import abm.co.domain.model.UserInterest
import abm.co.domain.repository.ServerRepository
import com.google.firebase.database.DatabaseReference
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class FirebaseRepositoryImp @Inject constructor(
    @Named(USER_REF) private var userDatabase: DatabaseReference,
    @Named(CONFIG_REF) private var config: DatabaseReference,
) : ServerRepository {

    override suspend fun setUserInfoAfterSignUp(
        email: String?,
        password: String?
    ): Either<Failure, Unit> {
        return Either.Empty
    }

    override suspend fun setUserGoal(userGoal: UserGoal): Either<Failure, Unit> {
        return Either.Empty
    }

    override suspend fun setUserInterests(
        userInterests: List<UserInterest>
    ): Either<Failure, Unit> {
        return Either.Empty
    }

    override suspend fun getUser(): Either<Failure, User> {
        return Either.Empty
    }

//    override suspend fun addCategories
}
