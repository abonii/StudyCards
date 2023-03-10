package abm.co.feature.userattributes.usergoal

data class UserGoal(
    val id: Int,
    val name: String
)

val userGoals = listOf(
    UserGoal(id = 0, "Изучить основы "),
    UserGoal(id = 1, "Посмотреть фильм на языке\n" +
        "(Английский(британский))"),
    UserGoal(id = 2, "Познакомиться с культурой языка"),
    UserGoal(id = 3, "Улучшить свой уровень"),
    UserGoal(id = 4, "Улучшить грамматику"),
    UserGoal(id = 5, "Сдать экзамены и ли тест"),
    UserGoal(id = 6, "Научиться говорить более свободно"),
    UserGoal(id = 7, "Понимать язык"),
    UserGoal(id = 8, "Что то")
)