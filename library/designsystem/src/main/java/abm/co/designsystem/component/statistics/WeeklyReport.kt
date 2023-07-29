package abm.co.designsystem.component.statistics

import abm.co.designsystem.base.WrapperList
import abm.co.designsystem.base.toWrapperList
import abm.co.designsystem.theme.StudyCardsTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun WeeklyReport(
    weekDaysWithProgress: WrapperList<Pair<String, Float>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = StudyCardsTheme.colors.backgroundPrimary,
                shape = RoundedCornerShape(22.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Weekly report", // todo
            style = StudyCardsTheme.typography.weight700Size15LineHeight20,
            color = StudyCardsTheme.colors.textPrimary,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            weekDaysWithProgress.forEach { (weekDay, progress) ->
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatableVerticalProgressView(
                            percent = progress,
                            modifier = Modifier.fillMaxHeight(),
                            width = 6.dp
                        )
                    }
                    Text(
                        text = weekDay,
                        style = StudyCardsTheme.typography.weight400Size10LineHeight11,
                        color = StudyCardsTheme.colors.textSecondary
                    )
                }
            }
        }
    }
}

@Preview(name = "Phone", device = "spec:width=411dp,height=891dp")
@Preview
@Composable
private fun WeeklyReport_Preview() {
    StudyCardsTheme {
        Box(modifier = Modifier.height(200.dp)) {
            WeeklyReport(
                modifier = Modifier
                    .padding(top = 50.dp, start = 16.dp, end = 16.dp)
                    .background(StudyCardsTheme.colors.uncertainStrong)
                    .fillMaxHeight()
                    .fillMaxWidth(.3f),
                weekDaysWithProgress = listOf(
                    "mo" to 0.4f,
                    "tu" to 0.2f,
                    "we" to 0.5f,
                    "th" to 0.6f,
                    "fr" to 0.3f,
                    "sn" to 1f,
                    "st" to 0.1f,
                ).toWrapperList()
            )
        }
    }
}