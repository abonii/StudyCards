package abm.co.studycards

import abm.co.studycards.data.PricingRepository
import abm.co.studycards.data.SubscriptionVerify
import abm.co.studycards.data.model.ConfirmText
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.util.Constants
import abm.co.studycards.util.Constants.MY_PURCHASES_REF
import abm.co.studycards.util.Constants.PURCHASES_REF
import abm.co.studycards.util.Constants.USERS_REF
import abm.co.studycards.util.base.BaseViewModel
import abm.co.studycards.util.toDay
import abm.co.studycards.util.toStartOfTheDay
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.android.billingclient.api.BillingClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.functions.FirebaseFunctions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Named


@HiltViewModel
class MainViewModel @Inject constructor(
    val prefs: Prefs,
    private val pricingRepository: PricingRepository,
    @Named(USERS_REF) val userDbRef: DatabaseReference,
    @Named(MY_PURCHASES_REF) val myPurchaseTokenDbRef: DatabaseReference,
    @Named(PURCHASES_REF) val purchaseRef: DatabaseReference
) : BaseViewModel() {

    var currentNavController: LiveData<NavController>? = null

    private val sourceLanguage = prefs.getSourceLanguage()
    private val targetLanguage = prefs.getTargetLanguage()

    init {
        myPurchaseTokenDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                    startPurchaseTokenListener(snapshot.key)
            }

            override fun onCancelled(error: DatabaseError) {
                makeToast("onCancelMyPurchase: " + error.message)
            }
        })
    }

    private fun startPurchaseTokenListener(value: String?) {
        if (value != null) {
            purchaseRef.child(value).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    makeToast(snapshot.children.toString())
                    if (snapshot.child("notificationType").exists()) {
                        (snapshot.child("notificationType").key as Int?)?.let {
                            checkSubscriptionType(it)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    makeToast("main start purchase: " + error.message)
                }
            })
        }
    }

    fun checkSubscriptionType(type: Int) {
        makeToast("notification type: $type")
        when (type) {
            4, 1, 2, 6, 7, 8, 9 -> prefs.setIsPremium(true)
            else -> prefs.setIsPremium(false)
        }
    }

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
                    val count = userDbRef.child("canTranslateTimeEveryDay")
                    val time = userDbRef.child("canTranslateTimeInMills")
                    val startOfToday = Calendar.getInstance().toStartOfTheDay()
                    val yesterdayCalendar = startOfToday.toDay(-1)
                    if (!snapshot.child("canTranslateTimeEveryDay").exists()) {
                        if (getCurrentUser()?.isAnonymous == true) {
                            count.setValue(Constants.CAN_TRANSLATE_EVERY_DAY_ANONYMOUS)
                        } else {
                            count.setValue(Constants.CAN_TRANSLATE_EVERY_DAY)
                        }
                    } else {
                        userDbRef.updateChildren(mapOf("canTranslateTimeEveryDay" to Constants.CAN_TRANSLATE_EVERY_DAY))
                    }
                    if (!snapshot.child("canTranslateTimeInMills").exists()) {
                        time.setValue(startOfToday.timeInMillis)
                    } else if (snapshot.child("canTranslateTimeInMills").value != null
                        && (snapshot.value as Map<*, *>)["canTranslateTimeInMills"] as Long
                        <= yesterdayCalendar.timeInMillis
                    ) {
                        userDbRef.updateChildren(mapOf("canTranslateTimeInMills" to startOfToday.timeInMillis))
                        count.setValue(Constants.CAN_TRANSLATE_EVERY_DAY)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                makeToast(error.message)
            }
        })
    }
//
//    fun verifySubscription() {
//        val data = mapOf(
//            "sku_id" to BillingClient.SkuType.SUBS,
//            "purchase_token" to "purchaseToken12345ytjhgfdsewt42YGRW",
//            "package_name" to "abm.co.studycards",
//            "user_id" to "J1s64Mfj42NgJ9Xp8um7UA1bn5b2",
//        )
//        firebaseFunctions.getHttpsCallable("verifySubscription").call(data)
//            .continueWith { task ->
//                try {
//                    (task.result?.data as HashMap<*, *>).let {
//                        val verifySubscription = SubscriptionVerify(
//                            status = it["status"] as Int,
//                            message = it["message"] as String
//                        )
//                        makeToast(
//                            "verified from server: ${
//                                verifySubscription.message.takeLast(
//                                    50
//                                )
//                            }"
//                        )
//                    }
//                } catch (e: Exception) {
//                    makeToast("some: ${e.message?.takeLast(70)}")
//                    null
//                }
//            }
//
//    }

    fun shutDownBillingClient() = pricingRepository.getBillingClient().endConnection()


}