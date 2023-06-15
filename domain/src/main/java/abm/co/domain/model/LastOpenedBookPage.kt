package abm.co.domain.model

data class LastOpenedBookPage(
    val bookUrl: String,
    val chapterUrl: String,
    val page: Int // starts from chapter
)
