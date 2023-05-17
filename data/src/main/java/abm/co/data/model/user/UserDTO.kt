package abm.co.data.model.user

import abm.co.domain.model.User
import androidx.annotation.Keep

@Keep
data class UserDTO(
    val name: String? = null,
    val email: String? = null,
    val translateCounts: Long? = null,
    val translateCountsUpdateTime: Long? = null,
    val goal: UserGoalDTO? = null,
    val interests: List<UserInterestDTO>? = null
)

fun UserDTO.toDomain() = User(
    name = name,
    email = email,
    translateCounts = translateCounts,
    translateCountsUpdateTime = translateCountsUpdateTime,
    goal = goal?.toDomain(),
    interests = interests?.map { it.toDomain() }
)