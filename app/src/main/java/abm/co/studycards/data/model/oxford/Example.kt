package abm.co.studycards.data.model.oxford

import abm.co.studycards.data.model.oxford.Translation
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Example(val text:String?, val translations:List<Translation>?): Parcelable
