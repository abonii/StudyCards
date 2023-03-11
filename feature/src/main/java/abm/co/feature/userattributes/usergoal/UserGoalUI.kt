package abm.co.feature.userattributes.usergoal

import abm.co.domain.model.UserGoal
import androidx.compose.runtime.Immutable

@Immutable
data class UserGoalUI(
    val id: Int,
    val title: String
)

fun UserGoalUI.toDomain() = UserGoal(
    id = id,
    title = title
)

fun UserGoal.toUI() = UserGoalUI(
    id = id,
    title = title
)

val defaultUserGoals = listOf(
    UserGoalUI(id = 0, "Изучить основы "),
    UserGoalUI(id = 1, "Посмотреть фильм на языке\n" +
        "(Английский(британский))"),
    UserGoalUI(id = 2, "Познакомиться с культурой языка"),
    UserGoalUI(id = 3, "Улучшить свой уровень"),
    UserGoalUI(id = 4, "Улучшить грамматику"),
    UserGoalUI(id = 5, "Сдать экзамены и ли тест"),
    UserGoalUI(id = 6, "Научиться говорить более свободно"),
    UserGoalUI(id = 7, "Понимать язык"),
    UserGoalUI(id = 8, "Что то")
)