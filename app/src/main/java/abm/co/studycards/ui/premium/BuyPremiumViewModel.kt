package abm.co.studycards.ui.premium

import abm.co.studycards.data.PricingRepository
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyPremiumViewModel @Inject constructor(
    val prefs: Prefs,
    private val pricingRepository: PricingRepository
) : BaseViewModel() {
    val skusStateFlow = pricingRepository.skusStateFlow
    fun getBillingClient() = pricingRepository.billingClient
    fun retryConnection() = viewModelScope.launch {
        pricingRepository.startConnection()
    }

    fun isUserAnonymous() = Firebase.auth.currentUser?.isAnonymous ?: true
    fun isUserNotVerified() = !(Firebase.auth.currentUser?.isEmailVerified ?: false)
}