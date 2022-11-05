package abm.co.studycards.data.model

data class UserInfoDto(
    val name: String = "",
    val translateCounts: Long = 0,
    val translateCountsUpdateTime: Long = 0,
    val email: String = "",
    val selectedLanguages: String = ""
) {
    companion object {
        const val CAN_TRANSLATE = "translateCounts"
        const val TRANSLATE_COUNT_UPDATE_TIME = "translateCountsUpdateTime"
        const val SELECTED_LANGUAGES = "selectedLanguages"
        const val EMAIL = "email"
        const val NAME = "name"
        const val SELECTED_LANGUAGES_SPLITTER = ","
    }
}