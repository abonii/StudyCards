package abm.co.data.model.config

import abm.co.domain.model.config.Config
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ConfigDTO(
    @SerializedName("translate_count")
    val translateCount: Int = 0,
    @SerializedName("oxford_id")
    val oxfordId: String = "",
    @SerializedName("oxford_key")
    val oxfordKey: String = "",
    @SerializedName("yandex_key")
    val yandexKey: String = "",
) {

    fun toDomain() = Config(
        translateCount = translateCount,
        oxfordId = oxfordId,
        oxfordKey = oxfordKey,
        yandexKey = yandexKey,
    )
}
