package abm.co.studycards.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ConfirmText: Parcelable {
    FINISH_REPEAT,
    FINISH_LEARN,
    FINISH_REVIEW,
    FINISH_GUESS,
    FINISH_PAIR,
    ON_EXIT
}