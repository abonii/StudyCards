package abm.co.studycards.data.model.vocabulary

import android.os.Parcelable
import android.util.Log
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: String = "",
    val mainName: String = "",
    var sourceLanguage: String = "",
    var targetLanguage: String = "",
    var words: List<Word> = emptyList()
) : Parcelable{

    fun getWord() = words

    fun setWord(words:Map<String, Word>) {
        this.words = ArrayList(words.values)
    }

}