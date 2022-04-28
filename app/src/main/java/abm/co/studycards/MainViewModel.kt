package abm.co.studycards

import abm.co.studycards.data.PricingRepository
import abm.co.studycards.data.model.ConfirmText
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.util.Constants
import abm.co.studycards.util.Constants.USERS_REF
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.toDay
import abm.co.studycards.util.toStartOfTheDay
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Named


@HiltViewModel
class MainViewModel @Inject constructor(
    val prefs: Prefs,
    @Named(USERS_REF) val userDbRef: DatabaseReference,
    pricingRepository: PricingRepository
) : BaseViewModel() {

    init {
        pricingRepository.startConnection()
    }

    var currentNavController: LiveData<NavController>? = null

    private val sourceLanguage = prefs.getSourceLanguage()
    private val targetLanguage = prefs.getTargetLanguage()

    fun isTargetAndSourceLangSet() = sourceLanguage.isBlank() || targetLanguage.isBlank()

    fun getCurrentUser() = FirebaseAuth.getInstance().currentUser

    fun onBackAndNavigateUp(): Boolean {
        return when (currentNavController?.value?.currentDestination?.id) {
            R.id.guessingFragment,
            R.id.matchingPairsFragment,
            R.id.toRightOrLeftFragment,
            R.id.addEditWordFragment,
            R.id.reviewFragment -> {
                currentNavController?.value?.navigate(
                    R.id.confirmEndFragment, bundleOf(Pair("confirmType", ConfirmText.ON_EXIT))
                )
                true
            }
            else -> false
        }
    }

    fun setDailyTranslateTime() {
        userDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModelScope.launch {
                    val count = userDbRef.child("canTranslateTimeEveryDay")
                    val time = userDbRef.child("canTranslateTimeInMills")
                    val startOfToday = Calendar.getInstance().toStartOfTheDay()
                    val yesterdayCalendar = startOfToday.toDay(-1)
                    if (getCurrentUser()?.isAnonymous == false
                        && !snapshot.child("email").exists()
                    ) {
                        userDbRef.child("email")
                            .setValue(getCurrentUser()?.email)
                    }
                    if (!snapshot.child("canTranslateTimeEveryDay").exists()) {
                        when (getCurrentUser()?.isAnonymous) {
                            true -> {
                                count.setValue(Constants.CAN_TRANSLATE_EVERY_DAY_ANONYMOUS)
                            }
                            else -> {
                                count.setValue(Constants.CAN_TRANSLATE_EVERY_DAY)
                            }
                        }
                    }
                    if (!snapshot.child("canTranslateTimeInMills").exists()) {
                        time.setValue(startOfToday.timeInMillis)
                    } else if (snapshot.child("canTranslateTimeInMills").value != null
                        && (snapshot.value as Map<*, *>)["canTranslateTimeInMills"] as Long
                        <= yesterdayCalendar.timeInMillis
                    ) {
                        userDbRef.updateChildren(mapOf("canTranslateTimeInMills" to startOfToday.timeInMillis))
                        val currentTimes = snapshot.child("canTranslateTimeEveryDay").value as Long
                        userDbRef.updateChildren(
                            mapOf(
                                "canTranslateTimeEveryDay" to when {
                                    currentTimes > Constants.CAN_TRANSLATE_EVERY_DAY -> {
                                        currentTimes + Constants.CAN_TRANSLATE_EVERY_DAY_ANONYMOUS
                                    }
                                    getCurrentUser()?.isAnonymous == true -> {
                                        Constants.CAN_TRANSLATE_EVERY_DAY_ANONYMOUS
                                    }
                                    else -> {
                                        Constants.CAN_TRANSLATE_EVERY_DAY
                                    }
                                }
                            )
                        )
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}