package abm.co.feature.book.model

import abm.co.domain.model.library.Book
import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class BookUI(
    val id: String,
    val name: String,
    val description: String,
    val languageCode: String,
    val kind: String,
    val level: LevelUI,
    val image: String,
    val bannerImage: String,
    val link: String,
    val visible: Boolean
) : Parcelable {

    @Immutable
    enum class LevelUI {
        A1, A2, B1, B2, C1, C2;
    }
}

fun Book.toUI() = BookUI(
    id = id,
    name = name,
    description = description,
    languageCode = languageCode,
    level = level.toUI(),
    image = image,
    kind = kind,
    link = link,
    bannerImage = bannerImage,
    visible = visible
)

fun Book.Level.toUI() = when (this) {
    Book.Level.A1 -> BookUI.LevelUI.A1
    Book.Level.A2 -> BookUI.LevelUI.A2
    Book.Level.B1 -> BookUI.LevelUI.B1
    Book.Level.B2 -> BookUI.LevelUI.B2
    Book.Level.C1 -> BookUI.LevelUI.C1
    Book.Level.C2 -> BookUI.LevelUI.C2
}