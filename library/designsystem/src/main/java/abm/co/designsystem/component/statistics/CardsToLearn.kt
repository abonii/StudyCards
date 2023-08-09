package abm.co.designsystem.component.statistics

import abm.co.designsystem.base.WrapperList
import abm.co.designsystem.base.toWrapperList
import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.theme.StudyCardsTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CardsToLearn(
    cardsCount: Int,
    colorPoints: WrapperList<Pair<Float, Color>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = StudyCardsTheme.colors.backgroundPrimary,
                shape = RoundedCornerShape(22.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Cards to learn", // todo
            style = StudyCardsTheme.typography.weight700Size15LineHeight20,
            color = StudyCardsTheme.colors.textPrimary,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = cardsCount.toString(),
                style = StudyCardsTheme.typography.weight500Size23LineHeight24,
                color = StudyCardsTheme.colors.textPrimary
            )
            Text(
                text = "words", // todo
                style = StudyCardsTheme.typography.weight500Size14LineHeight20,
                color = StudyCardsTheme.colors.textSecondary
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        AnimatableProgressView(
            modifier = Modifier.fillMaxWidth(),
            colorPoints = colorPoints
        )
    }
}

@Preview(name = "Phone", device = "spec:width=411dp,height=891dp")
@Preview
@Composable
private fun CardsToLearn_Preview() {
    StudyCardsTheme() {
        CardsToLearn(
            cardsCount = 3,
            colorPoints = listOf(
                0.5f to StudyCardsTheme.colors.success,
                0.3f to StudyCardsTheme.colors.uncertainStrong,
                0.2f to StudyCardsTheme.colors.unknown,
            ).toWrapperList(),
            modifier = Modifier.padding(top = 50.dp, start = 16.dp, end = 16.dp)
        )
    }
}
