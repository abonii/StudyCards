package abm.co.studycards.data.model.oxford

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sense(val id:String?, val notes:List<Note>?, val translations:List<Translation>?, val examples:List<Example>?,
): Parcelable
