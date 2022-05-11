package abm.co.studycards.data.model.oxford

import abm.co.studycards.util.stripAccents
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Translation(val language:String?, val text:String?): Parcelable{
    fun getNormalTranslation() = text?.stripAccents()
}
