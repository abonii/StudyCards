package abm.co.studycards.ui.premium

import abm.co.studycards.data.PricingRepository
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.util.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BuyPremiumViewModel @Inject constructor(
    val prefs: Prefs,
    private val pricingRepository: PricingRepository,
    private val firebaseAuth: FirebaseAuth
) : BaseViewModel() {
    val skusStateFlow = pricingRepository.skusStateFlow
    fun getBillingClient() = pricingRepository.billingClient
    fun isUserAnonymous() = firebaseAuth.currentUser?.isAnonymous ?: true
    fun isUserNotVerified() = !(firebaseAuth.currentUser?.isEmailVerified ?: false)
}