package abm.co.data.model.user

import abm.co.domain.model.UserGoal
import androidx.annotation.Keep

@Keep
data class UserGoalDTO(
    val id: Int,
    val title: String
) {

    companion object {
        const val id = "id"
        const val title = "title"
    }
}

fun UserGoalDTO.toDomain() = UserGoal(
    id = id,
    title = title
)

fun UserGoal.toDTO() = UserGoalDTO(
    id = id,
    title = title
)
