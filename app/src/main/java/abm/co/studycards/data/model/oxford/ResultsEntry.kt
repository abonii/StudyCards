package abm.co.studycards.data.model.oxford

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResultsEntry(
    val language: String?, val lexicalEntries: List<LexicalEntry>?, val word: String?
) : Parcelable