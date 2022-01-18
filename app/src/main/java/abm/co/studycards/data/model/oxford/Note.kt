package abm.co.studycards.data.model.oxford

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(val text:String?, val type:String?): Parcelable
