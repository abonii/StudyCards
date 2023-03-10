package abm.co.feature.userattributes.userinterest

data class UserInterest(
    val id: Int,
    val title: String,
    val isSelected: Boolean = false
)

val userInterests = listOf(
    UserInterest(id = 0, title = "Шоппинг"),
    UserInterest(id = 1, title = "Кино"),
    UserInterest(id = 2, title = "Мода"),
    UserInterest(id = 3, title = "Игры"),
    UserInterest(id = 4, title = "Путешествия"),
    UserInterest(id = 5, title = "еда"),
    UserInterest(id = 6, title = "Музыка"),
)