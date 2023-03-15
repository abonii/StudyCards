package abm.co.domain.repository

import abm.co.domain.base.Either
import abm.co.domain.base.Failure
import abm.co.domain.model.User
import abm.co.domain.model.UserGoal
import abm.co.domain.model.UserInterest
import kotlinx.coroutines.flow.Flow

interface ServerRepository {
    suspend fun getUser(): Flow<Either<Failure, User?>>
}
