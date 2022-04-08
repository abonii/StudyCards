package abm.co.studycards.data.model.vocabulary

import abm.co.studycards.data.model.LearnOrKnown
import abm.co.studycards.util.Constants.EXAMPLES_SEPARATOR
import abm.co.studycards.util.Constants.TRANSLATIONS_SEPARATOR
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Word(
    var name: String = "",
    var translations: List<String> = emptyList(),
    val imageUrl: String = "",
    var examples: List<String> = emptyList(),
    var learnOrKnown: String = LearnOrKnown.UNDEFINED.getType(),
    var sourceLanguage: String = "",
    var targetLanguage: String = "",
    val categoryID: String = "",
    var repeatCount: Int = 0,
    var nextRepeatTime: Long = 0,
    val wordId: String = ""
) : Parcelable {
    companion object {
        const val LEARN_OR_KNOWN = "learnOrKnown"
        const val REPEAT_COUNT = "repeatCount"
        const val NEXT_REPEAT_TIME = "nextRepeatTime"
    }

    fun setTranslation(translations: Map<String, String>) {
        this.translations = ArrayList(translations.values)
    }

    fun setExample(examples: Map<String, String>) {
        this.examples = ArrayList(examples.values)
    }
}

fun Word?.examplesToString() = this?.examples?.joinToString(EXAMPLES_SEPARATOR) ?: ""
fun Word?.translationsToString() = this?.translations?.joinToString(TRANSLATIONS_SEPARATOR) ?: ""
fun String.examplesToList() = this.split(EXAMPLES_SEPARATOR)
fun String.translationsToList() = this.split(TRANSLATIONS_SEPARATOR)