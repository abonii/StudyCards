package abm.co.domain.model

data class User(
    val name: String?,
    val email: String?,
    val translateCounts: Long?,
    val translateCountsUpdateTime: Long?,
    val goals: List<UserGoal>?,
    val interests: List<UserInterest>?
)
