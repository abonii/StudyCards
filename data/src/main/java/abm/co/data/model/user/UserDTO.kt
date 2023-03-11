package abm.co.data.model.user

import abm.co.domain.model.User
import androidx.annotation.Keep

@Keep
data class UserDTO(
    val name: String? = null,
    val email: String? = null,
    val translateCounts: Long? = null,
    val translateCountsUpdateTime: Long? = null,
    val goals: List<UserGoalDTO>? = null,
    val interests: List<UserInterestDTO>? = null
) {
    companion object {
        const val name = "name"
        const val email = "email"
        const val translateCounts = "translateCounts"
        const val translateCountsUpdateTime = "translateCountsUpdateTime"
        const val goals = "goals"
        const val interests = "interests"
        const val password = "password"
    }
}

fun UserDTO.toDomain() = User(
    name = name,
    email = email,
    translateCounts = translateCounts,
    translateCountsUpdateTime = translateCountsUpdateTime,
    goals = goals?.map { it.toDomain() },
    interests = interests?.map { it.toDomain() }
)