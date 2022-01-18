package abm.co.studycards.data.model.oxford

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pronunciation(
    val audioFile:String?,
    val dialects:List<String>?,
    val phoneticSpelling:String?
): Parcelable
