package abm.co.data.model.card

import abm.co.domain.model.Category
import androidx.annotation.Keep

@Keep
data class CategoryDTO(
    val name: String = "",
    val cardsCount: Int = 0,
    val bookmarked: Boolean = false,
    val creatorName: String? = null,
    val creatorID: String? = null,
    val imageURL: String? = null,
    val id: String = ""
)

fun CategoryDTO.toDomain() = Category(
    name = name,
    cardsCount = cardsCount,
    bookmarked = bookmarked,
    creatorName = creatorName,
    creatorID = creatorID,
    imageURL = imageURL,
    id = id
)

fun Category.toDTO() = CategoryDTO(
    name = name,
    cardsCount = cardsCount,
    bookmarked = bookmarked,
    creatorName = creatorName,
    creatorID = creatorID,
    imageURL = imageURL,
    id = id
)