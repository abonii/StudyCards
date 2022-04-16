package abm.co.studycards.ui.premium

import abm.co.studycards.data.PricingRepository
import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.domain.BillingUpdateListener
import abm.co.studycards.util.base.BaseViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyPremiumViewModel @Inject constructor(
    val prefs: Prefs,
    private val pricingRepository: PricingRepository,
    private val updateListener: BillingUpdateListener,
) : BaseViewModel() {

    init {
        viewModelScope.launch {
            updateListener.purchaseUpdateLiveData.collectLatest {
                pricingRepository.acknowledgePurchases(it)
            }
        }
    }

    private val _subscriptionsLiveData = MutableLiveData<List<SkuDetails>?>()
    val subscriptionsLiveData: LiveData<List<SkuDetails>?> = _subscriptionsLiveData

    fun getSubscriptions() {
        viewModelScope.launch(Dispatchers.IO) {
            _subscriptionsLiveData.postValue(pricingRepository.getSubscriptions())
        }
    }

    fun getBillingClient() = pricingRepository.getBillingClient()
}