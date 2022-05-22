package abm.co.studycards.data.model.vocabulary

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: String = "",
    val mainName: String = "",
    var sourceLanguage: String = "",
    var targetLanguage: String = "",
    val imageUrl: String = "",
    val creatorName:String = "_",
    val creatorId:String = "_",
    var words: List<Word> = emptyList(),
) : Parcelable {

    companion object{
        const val MAIN_NAME = "mainName"
    }

}