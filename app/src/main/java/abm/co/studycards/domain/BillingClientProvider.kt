package abm.co.studycards.domain

import com.android.billingclient.api.BillingClient

interface BillingClientProvider {
    var billingClient: BillingClient
}