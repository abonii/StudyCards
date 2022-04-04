package abm.co.studycards

import abm.co.studycards.data.model.ConfirmText
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.util.Constants
import abm.co.studycards.util.base.BaseViewModel
import androidx.core.os.bundleOf
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MainViewModel @Inject constructor(
    prefs: Prefs,
    @Named(Constants.USERS_REF)
    val userDbRef: DatabaseReference
) : BaseViewModel() {

    var currentNavController: NavController? = null

    private val sourceLanguage = prefs.getSourceLanguage()
    private val targetLanguage = prefs.getTargetLanguage()

    fun isTargetAndSourceLangSet() = sourceLanguage.isBlank() || targetLanguage.isBlank()

    fun getCurrentUser() = FirebaseAuth.getInstance().currentUser

    fun setDailyTranslateTime() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentCalendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            val yesterdayCalendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }
            val count = userDbRef.child("canTranslateTimeEveryDay")
            val time = userDbRef.child("canTranslateTimeInMills")
            if (count.get().await().value == null) {
                if (getCurrentUser()?.isAnonymous == true) {
                    count.setValue(Constants.CAN_TRANSLATE_EVERY_DAY_ANONYMOUS)
                } else count.setValue(Constants.CAN_TRANSLATE_EVERY_DAY)
            }
            if (time.get().await().value == null) {
                time.setValue(currentCalendar.timeInMillis)
            } else if (time.get().await().value as Long <= yesterdayCalendar.timeInMillis) {
                userDbRef.updateChildren(mapOf("canTranslateTimeInMills" to currentCalendar.timeInMillis))
                userDbRef.updateChildren(mapOf("canTranslateTimeEveryDay" to Constants.CAN_TRANSLATE_EVERY_DAY))
            }
        }
    }

    fun onBackAndNavigateUp(): Boolean {
        return when (currentNavController?.currentDestination?.id) {
            R.id.guessingFragment,
            R.id.matchingPairsFragment,
            R.id.toRightOrLeftFragment,
            R.id.reviewFragment -> {
                currentNavController?.navigate(
                    R.id.confirmEndFragment, bundleOf(Pair("confirmType", ConfirmText.ON_EXIT))
                )
                true
            }
            else -> false
        }
    }

}