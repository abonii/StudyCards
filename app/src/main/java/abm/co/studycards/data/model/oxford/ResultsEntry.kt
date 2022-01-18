package abm.co.studycards.data.model.oxford

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResultsEntry(
    val id: String?, val language: String?, val lexicalEntries: List<LexicalEntry>?
) : Parcelable
