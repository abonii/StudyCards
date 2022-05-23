package abm.co.studycards.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryDetails(
    val translations: List<String>,
    val examples: List<String>
):Parcelable