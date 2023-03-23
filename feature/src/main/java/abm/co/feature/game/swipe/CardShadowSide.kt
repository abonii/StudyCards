package abm.co.feature.game.swipe

sealed interface CardShadowSide {
    object ShadowStart : CardShadowSide
    object ShadowEnd : CardShadowSide
    object ShadowTop : CardShadowSide
    object ShadowBottom : CardShadowSide
}