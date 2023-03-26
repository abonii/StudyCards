package abm.co.data.model.card

import abm.co.domain.model.CardKind
import androidx.annotation.Keep

@Keep
enum class CardKindDTO {
    UNDEFINED,
    UNKNOWN,
    UNCERTAIN,
    KNOWN;
}

fun CardKind.toDTO() = when(this){
    CardKind.UNDEFINED -> {
        CardKindDTO.UNDEFINED
    }
    CardKind.UNKNOWN -> {
        CardKindDTO.UNKNOWN
    }
    CardKind.UNCERTAIN -> {
        CardKindDTO.UNCERTAIN
    }
    CardKind.KNOWN -> {
        CardKindDTO.KNOWN
    }
}

fun CardKindDTO.toDomain() = when(this){
    CardKindDTO.UNDEFINED -> {
        CardKind.UNDEFINED
    }
    CardKindDTO.UNKNOWN -> {
        CardKind.UNKNOWN
    }
    CardKindDTO.UNCERTAIN -> {
        CardKind.UNCERTAIN
    }
    CardKindDTO.KNOWN -> {
        CardKind.KNOWN
    }
}
