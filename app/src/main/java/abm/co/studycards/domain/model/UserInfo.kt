package abm.co.studycards.domain.model

data class UserInfo(
    val name: String,
    val translateCounts: Long,
    val translateCountsUpdateTime: Long,
    val email: String,
    val selectedLanguages: List<String>
)