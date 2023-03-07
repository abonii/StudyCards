package abm.co.studycards.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LexicalCategory(
    val lexicalName:String,
    val details:List<CategoryDetails>
) : Parcelable