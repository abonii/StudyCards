package abm.co.feature.game.swipe

sealed interface SwipeSide {
    object START : SwipeSide
    object TOP : SwipeSide
    object END : SwipeSide
    object BOTTOM : SwipeSide
}