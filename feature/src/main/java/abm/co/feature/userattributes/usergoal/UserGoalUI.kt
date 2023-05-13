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
    UserGoalUI(id = "Learn Basics", "Изучить основы"),
    UserGoalUI(id = "Watch movies", "Посмотреть фильм на языке"),
    UserGoalUI(id = "Watch movies", "Познакомиться с культурой языка"),
    UserGoalUI(id = "Leveling up", "Улучшить свой уровень"),
    UserGoalUI(id = "Learn grammar", "Улучшить грамматику"),
    UserGoalUI(id = "Pass exam", "Сдать экзамены и ли тест"),
    UserGoalUI(id = "Speak fluent", "Научиться говорить более свободно"),
    UserGoalUI(id = "Understand language", "Понимать язык"),
    UserGoalUI(id = "I don't want to tell", "Что то")
)