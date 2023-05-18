package abm.co.feature.game.review.model

import abm.co.feature.card.model.CardUI
import androidx.compose.runtime.Immutable

@Immutable
data class ReviewItemUI(
    val cardID: String,
    val imageURL: String,
    val title: String,
    val translation: String
)

fun CardUI.toReviewItem() = ReviewItemUI(
    cardID = cardID,
    title = name,
    imageURL = imageUrl,
    translation = translation
)