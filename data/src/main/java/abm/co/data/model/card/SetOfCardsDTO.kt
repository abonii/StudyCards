package abm.co.data.model.card

import abm.co.domain.model.Category
import androidx.annotation.Keep

@Keep
data class CategoryDTO(
    val name: String,
    val cardsCount: Int,
    val isBookmarked: Boolean,
    val creatorName: String?,
    val creatorID: String?,
    val imageURL: String?,
    val id: String
)

fun CategoryDTO.toDomain() = Category(
    name = name,
    cardsCount = cardsCount,
    isBookmarked = isBookmarked,
    creatorName = creatorName,
    creatorID = creatorID,
    imageURL = imageURL,
    id = id
)