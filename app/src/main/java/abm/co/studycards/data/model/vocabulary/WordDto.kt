package abm.co.studycards.data.model.vocabulary

import abm.co.studycards.domain.model.LearnOrKnown

data class WordDto(
    var name: String = "",
    var translations: String = "",
    val imageUrl: String = "",
    var examples: String = "",
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
}