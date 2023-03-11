package abm.co.feature.userattributes.userinterest

import abm.co.domain.model.UserInterest
import androidx.compose.runtime.Immutable

@Immutable
data class UserInterestUI(
    val id: Int,
    val title: String,
    val isSelected: Boolean = false
)

fun UserInterest.toUI() = UserInterestUI(
    id = id,
    title = title
)

fun UserInterestUI.toDomain() = UserInterest(
    id = id,
    title = title
)

val defaultUserInterests = listOf(
    UserInterestUI(id = 0, title = "Шоппинг"),
    UserInterestUI(id = 1, title = "Кино"),
    UserInterestUI(id = 2, title = "Мода"),
    UserInterestUI(id = 3, title = "Игры"),
    UserInterestUI(id = 4, title = "Путешествия"),
    UserInterestUI(id = 5, title = "еда"),
    UserInterestUI(id = 6, title = "Музыка"),
)