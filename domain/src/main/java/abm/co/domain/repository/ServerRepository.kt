package abm.co.domain.repository

import abm.co.domain.base.Either
import abm.co.domain.base.Failure
import abm.co.domain.model.UserInterest
import abm.co.domain.model.User
import abm.co.domain.model.UserGoal

interface ServerRepository {
    suspend fun setUserInfoAfterSignUp(email: String?, password: String?): Either<Failure, Unit>
    suspend fun setUserGoal(userGoal: UserGoal): Either<Failure, Unit>
    suspend fun setUserInterests(userInterests: List<UserInterest>): Either<Failure, Unit>
    suspend fun getUser(): Either<Failure, User>
}
