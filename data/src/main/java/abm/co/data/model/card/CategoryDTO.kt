package abm.co.data.model.card

import abm.co.domain.model.Category
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CategoryDTO(
    val title: String = "",
    val bookmarked: Boolean = false,
    val creatorName: String? = null,
    val creatorID: String? = null,
    val imageURL: String? = null,
    val id: String = ""
)

fun CategoryDTO.toDomain(cardsCount: Int) = Category(
    title = title,
    cardsCount = cardsCount,
    bookmarked = bookmarked,
    creatorName = creatorName,
    creatorID = creatorID,
    imageURL = imageURL,
    id = id
)

fun Category.toDTO() = CategoryDTO(
    title = title,
    bookmarked = bookmarked,
    creatorName = creatorName,
    creatorID = creatorID,
    imageURL = imageURL,
    id = id
)