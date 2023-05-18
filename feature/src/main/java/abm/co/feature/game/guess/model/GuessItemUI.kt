package abm.co.feature.game.guess.model

import abm.co.feature.card.model.CardUI
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
data class GuessItemUI(
    val cardID: String,
    val question: String,
    val answers: List<Answer>
) {

    @Immutable
    data class Answer(
        val cardID: String,
        val title: String
    )
}

fun Array<CardUI>.toGuessItems() = map { card ->
    GuessItemUI(
        cardID = card.cardID,
        question = card.translation,
        answers = map {
            GuessItemUI.Answer(
                cardID = it.cardID,
                title = it.name
            )
        }.shuffled()
    )
}
