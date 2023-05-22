package abm.co.feature.userattributes.usergoal

import abm.co.domain.model.UserGoal
import androidx.compose.runtime.Immutable

@Immutable
data class UserGoalUI(
    val id: String,
    val title: String
)

fun UserGoalUI.toDomain() = UserGoal(
    id = id
)

val defaultUserGoals = listOf( // todo what to do
    UserGoalUI(id = "Learn Basics", "Learn the basics"),
    UserGoalUI(id = "Watch movies", "Watch movies"),
    UserGoalUI(id = "learn culture of language", "Get to know the culture of the language"),
    UserGoalUI(id = "Leveling up", "Improve my level"),
    UserGoalUI(id = "Learn grammar", "Improve grammar"),
    UserGoalUI(id = "Pass exam", "Pass exam"),
    UserGoalUI(id = "Speak fluent", "To speak fluent"),
    UserGoalUI(id = "Understand language", "Understand language"),
    UserGoalUI(id = "I don't want to tell", "Something else")
)