package abm.co.studycards.domain.model

data class Config(
    val oxfordId: String,
    val yandexKey: String,
    val oxfordKey: String,
    val translateCount: Long,
    val translateCountAnonymous: Long,
)