package abm.co.studycards.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OxfordResult(
    val word: String,
    val lexicalCategories: List<LexicalCategory>
) : Parcelable