package abm.co.feature.card.model

import abm.co.domain.model.CardItem
import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class CardItemUI(
    val name: String,
    val translation: String,
    val kind: CardKindUI,
    val categoryID: String,
    val learnedPercent: Float, // 0..1
    val nextRepeatTime: Long,
    val id: String
): Parcelable

fun CardItem.toUI() = CardItemUI(
    name = name,
    translation = translation,
    kind = kind.toUI(),
    categoryID = categoryID,
    learnedPercent = learnedPercent,
    nextRepeatTime = nextRepeatTime,
    id = id
)