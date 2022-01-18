package abm.co.studycards.data.model.vocabulary

data class WordWithSelected(
    var word: Word,
    var isSelected: Boolean = false
)
data class CategoryWithSelected(
    var category: Category,
    var isSelected: Boolean = false
)