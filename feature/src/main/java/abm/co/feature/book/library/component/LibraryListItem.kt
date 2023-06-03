package abm.co.feature.book.library.component

import abm.co.designsystem.component.modifier.clickableWithoutRipple
import abm.co.designsystem.theme.StudyCardsTheme
import abm.co.feature.book.model.BookUI
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun LibraryListItem(
    onClick: () -> Unit,
    book: BookUI,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(dimensionResource(id = abm.co.designsystem.R.dimen.default_120dp))
            .clickableWithoutRipple(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            backgroundColor = StudyCardsTheme.colors.backgroundPrimary,
            elevation = 10.dp
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.656f),
                model = book.image,
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
        }
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = book.name,
            style = StudyCardsTheme.typography.weight500Size16LineHeight20,
            textAlign = TextAlign.Center,
            color = StudyCardsTheme.colors.textSecondary,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showSystemUi = true, showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun LibraryListItem_Prev() {
    StudyCardsTheme {
        LibraryListItem(
            book = BookUI(
                id = "",
                name = "Alice in wonderland",
                description = "",
                languageCode = "",
                kind = "Fantasy",
                level = BookUI.LevelUI.A1,
                image = "gs://study-cards-0000.appspot.com/library/cover/alice_adventures_in_wonderland.png",
                link = "",
                bannerImage = ""
            ),
            onClick = {}
        )
    }
}