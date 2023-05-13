package abm.co.data.model.user

import abm.co.domain.model.UserGoal
import androidx.annotation.Keep
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import org.json.JSONObject

@Keep
data class UserGoalDTO(
    val id: String
)

fun UserGoalDTO.toDomain() = UserGoal(
    id = id
)

fun UserGoal.toDTO() = UserGoalDTO(
    id = id
)