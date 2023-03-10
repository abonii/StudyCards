package abm.co.domain.repository

import abm.co.domain.base.Either
import abm.co.domain.base.Failure

interface ServerRepository {
    suspend fun setUserInfoAfterSignUp(email: String?, password: String?): Either<Failure, Unit>
}
