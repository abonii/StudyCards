package abm.co.studycards.data.model

data class ConfigDto(
    val oxfordId: String = "",
    val yandexKey: String = "",
    val oxfordKey: String = "",
    val translateCount: Long = 0,
    val translateCountAnonymous: Long = 0,
)