package abm.co.studycards.data.model.vocabulary

import abm.co.studycards.data.model.LearnOrKnown
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Word(
    var name: String = "",
    var translation: List<String> = emptyList(),
    val imageUrl: String? = null,
    var examples: List<String>? = null,
    var learnOrKnown: String = LearnOrKnown.UNDEFINED.getType(),
    var sourceLanguage: String = "",
    var targetLanguage: String = "",
    val categoryID: String = "",
    var repeatCount: Int = 0,
    var nextRepeatTime: Long = 0,
    val wordId: String = ""
) : Parcelable