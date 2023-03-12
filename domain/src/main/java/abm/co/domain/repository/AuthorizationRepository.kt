package abm.co.domain.repository

import abm.co.domain.model.UserGoal
import abm.co.domain.model.UserInterest

interface AuthorizationRepository {
    suspend fun setUserInfo(name: String?, email: String?, password: String?)
    suspend fun setUserGoal(userGoal: UserGoal)
    suspend fun setUserInterests(userInterests: List<UserInterest>)
}
