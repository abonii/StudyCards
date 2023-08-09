package abm.co.feature.card.component

import abm.co.designsystem.component.modifier.Modifier
import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.component.modifier.scalableClick
import abm.co.domain.functional.safeLet
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.R
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun CategoryItem(
    title: String,
    subtitle: String,
    isBookmarked: Boolean?,
    modifier: Modifier = Modifier,
    icon: String? = null,
    onClickBookmark: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onClickPlay: (() -> Unit)? = null,
    onClickShare: (() -> Unit)? = null,
    isPublished: Boolean? = null
) {
    Box {
        val pressed = rememberSaveable { mutableStateOf(false) }
        val scale = animateFloatAsState(if (pressed.value) 0.95f else 1f)
        Spacer(
            modifier = Modifier
                .scalableClick(
                    pressed = pressed,
                    onClick = { onClick?.invoke() },
                    onLongClick = { onLongClick?.invoke() }
                )
                .matchParentSize()
        )
        Row(
            modifier = modifier
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                }
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(22.dp),
                    ambientColor = Color.Transparent,
                    spotColor = StudyCardsTheme.colors.opposition
                )
                .background(
                    color = StudyCardsTheme.colors.backgroundPrimary,
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(vertical = 16.dp)
        ) {
            safeLet(isBookmarked, onClickBookmark) { isBookmarked, onClickBookmark ->
                BookmarkIcon(
                    isBookmarked = isBookmarked,
                    onClick = onClickBookmark
                )
            } ?: Spacer(modifier = Modifier.width(10.dp))
            icon?.let {
                AsyncImage(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .size(50.dp)
                        .align(Alignment.CenterVertically),
                    model = it,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            safeLet(onClickBookmark, icon) { _, _ ->
                Spacer(modifier = Modifier.width(10.dp))
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = title,
                    style = StudyCardsTheme.typography.weight500Size16LineHeight20,
                    color = StudyCardsTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = subtitle,
                    style = StudyCardsTheme.typography.weight400Size16LineHeight20,
                    color = StudyCardsTheme.colors.grayishBlack
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            safeLet(isPublished, onClickShare) { isPublished, onClickShare ->
                Icon(
                    modifier = Modifier
                        .clickableWithoutRipple(onClick = onClickShare)
                        .padding(top = 10.dp, bottom = 10.dp, end = 12.dp)
                        .size(24.dp)
                        .align(Alignment.CenterVertically),
                    painter = painterResource(id = R.drawable.ic_share),
                    tint = if (isPublished) StudyCardsTheme.colors.buttonPrimary
                    else StudyCardsTheme.colors.blueMiddle,
                    contentDescription = null
                )
            }
            onClickPlay?.let {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    modifier = Modifier
                        .clickableWithoutRipple(onClick = onClickPlay)
                        .padding(top = 10.dp, bottom = 10.dp, end = 12.dp)
                        .size(24.dp)
                        .align(Alignment.CenterVertically),
                    painter = painterResource(id = R.drawable.ic_play),
                    tint = StudyCardsTheme.colors.buttonPrimary,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun BookmarkIcon(
    isBookmarked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Icon(
        modifier = modifier
            .clickableWithoutRipple(onClick)
            .padding(start = 12.dp, bottom = 10.dp, end = 10.dp)
            .size(20.dp),
        painter = painterResource(id = R.drawable.ic_bookmark),
        contentDescription = null,
        tint = if (isBookmarked) StudyCardsTheme.colors.error
        else StudyCardsTheme.colors.blueMiddle
    )
}