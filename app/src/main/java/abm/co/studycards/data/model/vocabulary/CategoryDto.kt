package abm.co.studycards.data.model.vocabulary

class CategoryDto(
    val id: String = "",
    val mainName: String = "",
    var sourceLanguage: String = "",
    var targetLanguage: String = "",
    var creatorName: String = "",
    var creatorId: String = "",
    val imageUrl: String = "",
    _words: List<Word> = emptyList(),
) {

    var words: Any = _words
        set(value) {
            field = helperSet(value)
        }

    private fun <T> helperSet(t: T): ArrayList<*> = when (t) {
        is ArrayList<*> -> t
        is Map<*, *> -> ArrayList(t.values)
        else -> throw IllegalArgumentException()
    }

    @Suppress("UNCHECKED_CAST")
    fun toCategory() = Category(id, mainName, sourceLanguage, targetLanguage, imageUrl,
        creatorName,creatorId, words as List<Word>
    )


}