package abm.co.domain.model

data class User(
    val name: String?,
    val email: String?,
    val translateCounts: Long?,
    val translateCountsUpdateTime: Long?,
    val goal: UserGoal?,
    val interests: List<UserInterest>?
) {
    companion object {
        const val name = "name"
        const val email = "email"
        const val translateCounts = "translateCounts"
        const val goal = "goal"
        const val interests = "interests"
        const val password = "password"
    }
}