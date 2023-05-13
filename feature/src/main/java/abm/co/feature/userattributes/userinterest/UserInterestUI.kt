package abm.co.feature.userattributes.userinterest

import abm.co.domain.model.UserInterest
import androidx.compose.runtime.Immutable

@Immutable
data class UserInterestUI(
    val id: String,
    val title: String,
    val isSelected: Boolean = false
)

fun UserInterestUI.toDomain() = UserInterest(
    id = id
)

val defaultUserInterests = listOf( // todo what to do
    UserInterestUI(id = "Shopping", title = "Шоппинг"),
    UserInterestUI(id = "Movie", title = "Кино"),
    UserInterestUI(id = "Fashion", title = "Мода"),
    UserInterestUI(id = "Game", title = "Игры"),
    UserInterestUI(id = "Traveling", title = "Путешествия"),
    UserInterestUI(id = "Food", title = "Еда"),
    UserInterestUI(id = "Music", title = "Музыка"),
)