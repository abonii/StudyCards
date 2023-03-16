package abm.co.data.model.user

import abm.co.domain.model.UserInterest
import androidx.annotation.Keep

@Keep
data class UserInterestDTO(
    val id: Int,
    val title: String
)

fun UserInterest.toDTO() = UserInterestDTO(
    id = id,
    title = title
)

fun UserInterestDTO.toDomain() = UserInterest(
    id = id,
    title = title
)
