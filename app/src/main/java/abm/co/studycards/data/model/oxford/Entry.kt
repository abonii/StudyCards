package abm.co.studycards.data.model.oxford

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Entry(val senses: List<Sense>?
):Parcelable
