package abm.co.studycards.domain.model

import abm.co.studycards.util.Constants.EXAMPLES_SEPARATOR
import abm.co.studycards.util.Constants.TRANSLATIONS_SEPARATOR
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Word(
    var name: String,
    var translations: List<String>,
    val imageUrl: String,
    var examples: List<String>,
    var learnOrKnown: String,
    var sourceLanguage: String,
    var targetLanguage: String,
    val categoryID: String,
    var repeatCount: Int,
    var nextRepeatTime: Long,
    val wordId: String
) : Parcelable

fun Word?.examplesToString() = this?.examples?.joinToString(EXAMPLES_SEPARATOR) ?: ""
fun Word?.translationsToString() = this?.translations?.joinToString(TRANSLATIONS_SEPARATOR) ?: ""
fun String.examplesToList() = this.split(EXAMPLES_SEPARATOR)
fun String.translationsToList() = this.split(TRANSLATIONS_SEPARATOR)