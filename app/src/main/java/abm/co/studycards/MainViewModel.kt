package abm.co.studycards

import abm.co.studycards.data.PricingRepository
import abm.co.studycards.data.model.ConfirmText
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.data.repository.ServerCloudRepository
import abm.co.studycards.util.Constants
import abm.co.studycards.util.Constants.CAN_TRANSLATE_TIME_EVERY_DAY
import abm.co.studycards.util.Constants.CAN_TRANSLATE_TIME_IN_MILLS
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.firebaseError
import abm.co.studycards.util.toDay
import abm.co.studycards.util.toStartOfTheDay
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    val prefs: Prefs,
    firebaseRepository: ServerCloudRepository,
    pricingRepository: PricingRepository
) : BaseViewModel() {

    init {
        pricingRepository.startConnection()
        if (getCurrentUser()?.isAnonymous == false && getCurrentUser()?.isEmailVerified == false) {
            Firebase.auth.currentUser?.reload()
        }
    }

    var currentNavController: LiveData<NavController>? = null

    private val sourceLanguage = prefs.getSourceLanguage()
    private val targetLanguage = prefs.getTargetLanguage()

    private val userDbRef = firebaseRepository.getUserReference()

    fun isTargetAndSourceLangSet() = sourceLanguage.isBlank() || targetLanguage.isBlank()

    fun getCurrentUser() = FirebaseAuth.getInstance().currentUser

    fun onBackAndNavigateUp(): Boolean {
        return when (currentNavController?.value?.currentDestination?.id) {
            R.id.guessingFragment,
            R.id.matchingPairsFragment,
            R.id.toRightOrLeftFragment,
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
                    val count = userDbRef.child(CAN_TRANSLATE_TIME_EVERY_DAY)
                    val time = userDbRef.child(CAN_TRANSLATE_TIME_IN_MILLS)
                    val startOfToday = Calendar.getInstance().toStartOfTheDay()
                    val yesterdayCalendar = Calendar.getInstance().toDay(-1)
                    if (getCurrentUser()?.isAnonymous == false
                        && !snapshot.child("email").exists()
                    ) {
                        userDbRef.child("email")
                            .setValue(getCurrentUser()?.email)
                    }
                    if (!snapshot.child(CAN_TRANSLATE_TIME_EVERY_DAY).exists()) {
                        when (getCurrentUser()?.isAnonymous) {
                            true -> {
                                count.setValue(Constants.CAN_TRANSLATE_EVERY_DAY_ANONYMOUS)
                            }
                            else -> {
                                count.setValue(Constants.CAN_TRANSLATE_EVERY_DAY)
                            }
                        }
                    }
                    if (!snapshot.child(CAN_TRANSLATE_TIME_IN_MILLS).exists()) {
                        time.setValue(startOfToday.timeInMillis)
                    } else if (snapshot.child(CAN_TRANSLATE_TIME_IN_MILLS).value != null
                        && (snapshot.value as Map<*, *>)[CAN_TRANSLATE_TIME_IN_MILLS] as Long
                        <= yesterdayCalendar.timeInMillis
                    ) {
                        userDbRef.updateChildren(mapOf(CAN_TRANSLATE_TIME_IN_MILLS to startOfToday.timeInMillis))
                        val currentTimes =
                            snapshot.child(CAN_TRANSLATE_TIME_EVERY_DAY).value as Long
                        userDbRef.updateChildren(
                            mapOf(
                                CAN_TRANSLATE_TIME_EVERY_DAY to when {
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
                makeToast(firebaseError(error.code))
            }
        })
    }
}