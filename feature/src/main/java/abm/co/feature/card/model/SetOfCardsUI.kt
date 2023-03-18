package abm.co.feature.card.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class SetOfCardsUI(
    val name: String,
    val cardsCount: Int,
    val isFavorite: Boolean,
    val creatorName: String?,
    val creatorID: String?,
    val imageUrl: String?,
    val id: String
): Parcelable
