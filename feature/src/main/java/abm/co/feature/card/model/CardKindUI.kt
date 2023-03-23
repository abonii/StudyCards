package abm.co.feature.card.model

import abm.co.domain.model.CardKind

enum class CardKindUI {
    UNDEFINED,
    UNKNOWN,
    UNCERTAIN,
    KNOWN;
}

fun CardKindUI.toDomain() = when (this) {
    CardKindUI.UNDEFINED -> CardKind.UNDEFINED
    CardKindUI.UNKNOWN -> CardKind.UNKNOWN
    CardKindUI.UNCERTAIN -> CardKind.UNCERTAIN
    CardKindUI.KNOWN -> CardKind.KNOWN
}

fun CardKind.toUI() = when (this) {
    CardKind.UNDEFINED -> CardKindUI.UNDEFINED
    CardKind.UNKNOWN -> CardKindUI.UNKNOWN
    CardKind.UNCERTAIN -> CardKindUI.UNCERTAIN
    CardKind.KNOWN -> CardKindUI.KNOWN
}