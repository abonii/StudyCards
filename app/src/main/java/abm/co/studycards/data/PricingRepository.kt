package abm.co.studycards.data

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import kotlinx.coroutines.flow.StateFlow

interface PricingRepository {
    suspend fun getProducts(productDetailsParams: SkuDetailsParams): List<SkuDetails>?
    fun getBillingClient(): BillingClient
    suspend fun setPurchases(purchaseType: String)
    suspend fun getSubscriptions(): List<SkuDetails>?
    suspend fun acknowledgePurchases(list: List<Purchase>?)
    val purchaseStateFlow: StateFlow<List<Purchase>?>
}