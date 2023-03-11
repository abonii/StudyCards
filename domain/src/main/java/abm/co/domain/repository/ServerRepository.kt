package abm.co.domain.repository

import abm.co.domain.model.User
import abm.co.domain.model.UserGoal
import abm.co.domain.model.UserInterest
import kotlinx.coroutines.flow.Flow

interface ServerRepository {
    suspend fun setUserInfoAfterSignUp(email: String?, password: String?)
    suspend fun setUserGoal(userGoal: UserGoal)
    suspend fun setUserInterests(userInterests: List<UserInterest>)
    suspend fun getUser(): Flow<User>
}
