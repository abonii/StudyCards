package abm.co.data.model.user

import abm.co.domain.model.UserInterest
import androidx.annotation.Keep

@Keep
data class UserInterestDTO(
    val id: Int,
    val title: String
) {

    companion object {
        const val id = "id"
        const val title = "title"
    }
}

fun UserInterest.toDTO() = UserInterestDTO(
    id = id,
    title = title
)

fun UserInterestDTO.toDomain() = UserInterest(
    id = id,
    title = title
)
