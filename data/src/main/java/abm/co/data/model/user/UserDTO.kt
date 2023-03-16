package abm.co.data.model.user

import abm.co.domain.model.User
import androidx.annotation.Keep

@Keep
data class UserDTO(
    val name: String?,
    val email: String?,
    val translateCounts: Long?,
    val translateCountsUpdateTime: Long?,
    val goal: UserGoalDTO?,
    val interests: List<UserInterestDTO>?
)

fun UserDTO.toDomain() = User(
    name = name,
    email = email,
    translateCounts = translateCounts,
    translateCountsUpdateTime = translateCountsUpdateTime,
    goal = goal?.toDomain(),
    interests = interests?.map { it.toDomain() }
)