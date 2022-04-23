package abm.co.studycards.data

import com.android.billingclient.api.BillingResult

data class ConsumeProductResult(
    val billingResult: BillingResult,
    val purchaseToken: String
)