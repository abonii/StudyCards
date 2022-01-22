package abm.co.studycards.data.model.vocabulary

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: String = "",
    val mainName: String = "",
    var sourceLanguage: String = "",
    var targetLanguage: String = "",
    @JvmField var words: List<Word> = emptyList(),
) : Parcelable {
    companion object{
        const val MAIN_NAME = "mainName"
    }

    fun getWords() = words

    fun setWords(words: Map<String, Word>) {
        this.words = ArrayList(words.values)
    }

}