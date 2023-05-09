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
    val bookmarked: Boolean,
    val creatorName: String?,
    val creatorID: String?,
    val imageURL: String?,
    val id: String,
    val published: Boolean?
) : Parcelable

fun Category.toUI(published: Boolean? = null) = CategoryUI(
    name = name,
    cardsCount = cardsCount,
    bookmarked = bookmarked,
    creatorName = creatorName,
    creatorID = creatorID,
    imageURL = imageURL,
    id = id,
    published = published
)

fun CategoryUI.toDomain() = Category(
    name = name,
    cardsCount = cardsCount,
    bookmarked = bookmarked,
    creatorName = creatorName,
    creatorID = creatorID,
    imageURL = imageURL,
    id = id
)
