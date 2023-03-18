package abm.co.feature.card.component

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.disabledRippleClickable
import abm.co.designsystem.component.modifier.scalableClick
import abm.co.designsystem.component.text.pluralString
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
import abm.co.feature.card.model.SetOfCardsUI
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SetOfCardsItem(
    setOfCards: SetOfCardsUI,
    onClick: () -> Unit,
    onClickFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pressed = rememberSaveable { mutableStateOf(false) }
    val scale = animateFloatAsState(if (pressed.value) 0.95f else 1f)
    Box {
        Spacer(
            modifier = Modifier
                .scalableClick(
                    pressed = pressed,
                    onClick = onClick
                )
                .matchParentSize()
        )
        Row(
            modifier = modifier
                .scale(scale.value)
                .clip(RoundedCornerShape(10.dp))
                .background(StudyCardsTheme.colors.milky)
                .padding(end = 12.dp, top = 12.dp, bottom = 12.dp)
        ) {
            Icon(
                modifier = Modifier
                    .disabledRippleClickable(onClick = onClickFavorite)
                    .padding(start = 12.dp, bottom = 20.dp, end = 20.dp)
                    .size(20.dp),
                painter = painterResource(id = R.drawable.ic_bookmark),
                contentDescription = null,
                tint = if (setOfCards.isFavorite) StudyCardsTheme.colors.error
                else StudyCardsTheme.colors.blueMiddle
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = setOfCards.name,
                    color = StudyCardsTheme.colors.textPrimary,
                    style = StudyCardsTheme.typography.weight500Size16LineHeight20
                )
                Text(
                    text = pluralString(id = R.plurals.words, count = setOfCards.cardsCount),
                    color = StudyCardsTheme.colors.textSecondary,
                    style = StudyCardsTheme.typography.weight400Size16LineHeight20
                )
            }
            Spacer(modifier = Modifier.width(25.dp))
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterVertically),
                painter = painterResource(id = R.drawable.ic_play),
                contentDescription = null
            )
        }
    }
}
