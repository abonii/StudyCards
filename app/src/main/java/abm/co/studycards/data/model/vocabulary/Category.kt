package abm.co.studycards.data.model.vocabulary

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: String = "",
    val mainName: String = "",
    var sourceLanguage: String = "",
    var targetLanguage: String = "",
    val words: List<Word>? = null
) : Parcelable