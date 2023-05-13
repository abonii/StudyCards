package abm.co.data.model.user

import abm.co.domain.model.UserInterest
import androidx.annotation.Keep
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

@Keep
data class UserInterestDTO(
    val id: String
)

fun UserInterest.toDTO() = UserInterestDTO(
    id = id
)

fun UserInterestDTO.toDomain() = UserInterest(
    id = id
)
