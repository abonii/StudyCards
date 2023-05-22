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
    UserInterestUI(id = "Shopping", title = "Shopping"),
    UserInterestUI(id = "Movie", title = "Movie"),
    UserInterestUI(id = "Fashion", title = "Fashion"),
    UserInterestUI(id = "Games", title = "Games"),
    UserInterestUI(id = "Traveling", title = "Traveling"),
    UserInterestUI(id = "Food", title = "Food"),
    UserInterestUI(id = "Music", title = "Music"),
)