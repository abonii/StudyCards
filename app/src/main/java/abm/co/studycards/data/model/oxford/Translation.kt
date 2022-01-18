package abm.co.studycards.data.model.oxford

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Translation(val language:String?, val text:String?): Parcelable
