package abm.co.studycards.data.model.vocabulary

data class CategoryDto(
    val id: String = "",
    val name: String = "",
    var sourceLanguage: String = "",
    var targetLanguage: String = "",
    var creatorName: String = "",
    var creatorId: String = "",
    val imageUrl: String = "",
    val words: Map<String, WordDto> = emptyMap(),
)