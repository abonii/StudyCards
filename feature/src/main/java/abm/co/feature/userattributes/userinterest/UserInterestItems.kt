package abm.co.feature.userattributes.userinterest

import abm.co.designsystem.component.button.PrimaryButton
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.list.GridItems
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.userattributes.common.AnimatableContent
import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

private const val SPAN = 3

@Composable
fun UserInterestItems(
    visible: Boolean,
    userInterests: ImmutableList<UserInterestUI>,
    isToRight: Boolean,
    onClickItem: (UserInterestUI) -> Unit,
    onClickContinueButton: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatableContent(
        visible = visible,
        isToRight = isToRight
    ) {
        Column(modifier = modifier) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                GridItems(
                    data = userInterests,
                    spanCount = SPAN,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    key = { _, item -> item.id }
                ) { _, item, _, _ ->
                    UserInterestItem(
                        modifier = Modifier,
                        userInterest = item,
                        onClick = onClickItem
                    )
                }
            }
            PrimaryButton(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, bottom = 35.dp, top = 8.dp)
                    .fillMaxWidth(),
                title = stringResource(id = R.string.ChooseUserAttributesPage_UserInterests_continue),
                normalContentColor = StudyCardsTheme.colors.primary,
                normalButtonBackgroundColor = Color.White,
                onClick = onClickContinueButton
            )
        }
    }
}