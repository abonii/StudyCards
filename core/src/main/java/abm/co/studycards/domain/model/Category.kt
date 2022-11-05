package abm.co.studycards.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: String,
    val name: String,
    var sourceLanguage: String,
    var targetLanguage: String,
    val imageUrl: String,
    val creatorName: String,
    val creatorId: String,
    var words: List<Word>,
) : Parcelable {

    companion object {
        const val NAME = "name"
    }

}