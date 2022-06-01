package abm.co.studycards.domain.model

import abm.co.studycards.util.Constants.EXAMPLES_SEPARATOR
import abm.co.studycards.util.Constants.TRANSLATIONS_SEPARATOR
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Word(
    var name: String,
    var translations: String,
    val imageUrl: String,
    var examples: String,
    var learnOrKnown: String,
    var sourceLanguage: String,
    var targetLanguage: String,
    val categoryID: String,
    var repeatCount: Int,
    var nextRepeatTime: Long,
    val wordId: String
) : Parcelable
