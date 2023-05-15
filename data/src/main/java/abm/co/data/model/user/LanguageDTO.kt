package abm.co.data.model.user

import abm.co.domain.model.Language
import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class LanguageDTO(
    val code: String
)

fun LanguageDTO.toDomain() = Language(
    code = code
)

fun Language.toDTO() = LanguageDTO(
    code = code
)
