package abm.co.feature.userattributes.usergoal

import abm.co.designsystem.component.modifier.Modifier
import abm.co.feature.userattributes.common.AnimatableContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

@Composable
fun UserGoalItems(
    visible: Boolean,
    userGoals: ImmutableList<UserGoalUI>,
    isToRight: Boolean,
    onClickItem: (UserGoalUI) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatableContent(
        visible = visible,
        isToRight = isToRight
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            userGoals.forEach { userGoal ->
                UserGoalItem(
                    userGoal = userGoal,
                    onClick = onClickItem
                )
            }
        }
    }
}
