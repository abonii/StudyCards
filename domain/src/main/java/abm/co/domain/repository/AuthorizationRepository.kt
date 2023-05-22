package abm.co.domain.repository

import abm.co.domain.model.UserGoal
import abm.co.domain.model.UserInterest

interface AuthorizationRepository {
    suspend fun setUserInfo(name: String? = null, email: String? = null, password: String? = null)
    suspend fun setUserGoal(userGoal: UserGoal)
    suspend fun setUserInterests(userInterests: List<UserInterest>)
    suspend fun updateUserTranslationCount(count: Long)
    suspend fun addUserTranslationCount()
}
