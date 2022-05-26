package abm.co.studycards.data.model.vocabulary

import abm.co.studycards.domain.model.LearnOrKnown

data class WordDto(
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
) {
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