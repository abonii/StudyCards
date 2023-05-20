package abm.co.feature.game.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class GameKindUI: Parcelable {
    Review, Guess, Pair;
}
