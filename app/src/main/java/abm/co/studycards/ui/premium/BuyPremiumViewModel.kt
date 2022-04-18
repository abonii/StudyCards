package abm.co.studycards.ui.premium

import abm.co.studycards.data.PricingRepository
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.domain.BillingUpdateListener
import abm.co.studycards.util.Constants
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetails
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyPremiumViewModel @Inject constructor(
    val prefs: Prefs,
    private val pricingRepository: PricingRepository,
    private val updateListener: BillingUpdateListener,
    val firebaseAuth: FirebaseAuth
) : BaseViewModel() {

    private val _subscriptionsLiveData = MutableLiveData<List<SkuDetails>?>()
    val subscriptionsLiveData: LiveData<List<SkuDetails>?> = _subscriptionsLiveData

    init {
        viewModelScope.launch {
            updateListener.purchaseUpdateLiveData.collectLatest {
                makeToast("VM_PUR" + it.toString())
                pricingRepository.verifySubscriptions(it)
            }
            pricingRepository.verifySharedFlow.collectLatest {
                makeToast("VM_VER" + it.toString())
                pricingRepository.acknowledgePurchases(it)
            }
        }
    }

    fun getBillingClient() = pricingRepository.getBillingClient()
    fun isUserAnonymous() = firebaseAuth.currentUser?.isAnonymous ?: true
    fun isUserNotVerified() = !(firebaseAuth.currentUser?.isEmailVerified ?: false)
    fun getProducts() {
        viewModelScope.launch {
            _subscriptionsLiveData.postValue(pricingRepository.getSubscriptions())
        }
    }
}