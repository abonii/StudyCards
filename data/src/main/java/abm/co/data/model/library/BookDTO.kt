package abm.co.data.model.library

import abm.co.domain.model.library.Book
import com.google.errorprone.annotations.Keep

@Keep
data class BookDTO(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val languageCode: String = "",
    val level: LevelDTO = LevelDTO.A1,
    val bannerImage: String = "",
    val kind: String = "",
    val image: String = "",
    val link: String = ""
) {
    @Keep
    enum class LevelDTO {
        A1, A2, B1, B2, C1, C2;

        fun toDomain() = when (this) {
            A1 -> Book.Level.A1
            A2 -> Book.Level.A2
            B1 -> Book.Level.B1
            B2 -> Book.Level.B2
            C1 -> Book.Level.C1
            C2 -> Book.Level.C2
        }
    }

    fun toDomain() = Book(
        id = id,
        name = name,
        description = description,
        languageCode = languageCode,
        level = level.toDomain(),
        image = image,
        kind = kind,
        link = link,
        bannerImage = bannerImage
    )
}

fun Book.toDTO() = BookDTO(
    id = id,
    name = name,
    description = description,
    languageCode = languageCode,
    level = level.toDTO(),
    image = image,
    link = link,
    kind = kind,
    bannerImage = bannerImage
)

fun Book.Level.toDTO() = when (this) {
    Book.Level.A1 -> BookDTO.LevelDTO.A1
    Book.Level.A2 -> BookDTO.LevelDTO.A2
    Book.Level.B1 -> BookDTO.LevelDTO.B1
    Book.Level.B2 -> BookDTO.LevelDTO.B2
    Book.Level.C1 -> BookDTO.LevelDTO.C1
    Book.Level.C2 -> BookDTO.LevelDTO.C2
}