package abm.co.domain.model

data class User(
    val name: String?,
    val email: String?,
    val translateCounts: Long?,
    val translateCountsUpdateTime: Long?,
    val selectedLanguages: List<String>?,
    val reasonOfLearning: String?,
    val userInterests: List<UserInterest>?
)