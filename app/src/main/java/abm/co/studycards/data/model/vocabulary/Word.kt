package abm.co.studycards.data.model.vocabulary

import abm.co.studycards.data.model.LearnOrKnown
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
) : Parcelable{

    fun setTranslation(translations: Map<String, String>){
        this.translations = ArrayList(translations.values)
    }
    fun setExample(examples: Map<String, String>){
        this.examples = ArrayList(examples.values)
    }
}