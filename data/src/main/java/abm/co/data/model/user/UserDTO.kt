package abm.co.data.model.user

import androidx.annotation.Keep

@Keep
data class UserDTO(
    val name: String?,
    val email: String?,
    val translateCounts: Long?,
    val translateCountsUpdateTime: Long?,
    val reasonOfLearning: String?,
    val interests: List<InterestDTO>?
)
