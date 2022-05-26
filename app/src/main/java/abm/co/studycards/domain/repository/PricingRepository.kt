package abm.co.studycards.domain.repository

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetails
import kotlinx.coroutines.flow.StateFlow

interface PricingRepository {
    val skusStateFlow: StateFlow<List<SkuDetails>>
    val billingClient: BillingClient
    fun startConnection()
}