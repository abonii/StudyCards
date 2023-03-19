package abm.co.feature.card.model

import abm.co.domain.model.Category
import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class CategoryUI(
    val name: String,
    val cardsCount: Int,
    val isBookmarked: Boolean,
    val creatorName: String?,
    val creatorID: String?,
    val imageUrl: String?,
    val id: String
) : Parcelable

fun Category.toUI() = CategoryUI(
    name = name,
    cardsCount = cardsCount,
    isBookmarked = isBookmarked,
    creatorName = creatorName,
    creatorID = creatorID,
    imageUrl = imageURL,
    id = id
)
