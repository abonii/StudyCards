package abm.co.studycards.data.model.oxford

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LexicalEntry(
    val language: String?,
    val entries: List<Entry>?,
    val lexicalCategory: LexicalCategory?,
    val text: String?
) : Parcelable