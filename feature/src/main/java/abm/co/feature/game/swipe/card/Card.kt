package abm.co.feature.game.swipe.card

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.card.model.CardUI
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun FrontCardItem(
    cardUI: CardUI,
    isFront: Boolean,
    onClickUncertain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(StudyCardsTheme.colors.backgroundPrimary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = cardUI.translation,
                style = StudyCardsTheme.typography.weight600Size16LineHeight18,
                color = StudyCardsTheme.colors.textSecondary.copy(
                    if (isFront) {
                        1f
                    } else {
                        0.5f
                    }
                )
            )
        }
        Uncertain(
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = onClickUncertain
        )
    }
}

@Composable
fun BackCardItem(
    cardUI: CardUI,
    onClickUncertain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(StudyCardsTheme.colors.backgroundPrimary)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = cardUI.name,
                style = StudyCardsTheme.typography.weight600Size16LineHeight18,
                color = StudyCardsTheme.colors.textSecondary
            )
        }
        Uncertain(
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = onClickUncertain
        )
    }
}


@Composable
fun Uncertain(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(bottom = 28.dp)
            .clickableWithoutRipple(onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.Game_Swipe_uncertain),
            style = StudyCardsTheme.typography.weight400Size14LineHeight18,
            color = StudyCardsTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = R.drawable.ic_arrow_down),
            tint = StudyCardsTheme.colors.textSecondary,
            contentDescription = null
        )
    }
}
