package abm.co.studycards.domain

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingClientProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    updateListener: PurchasesUpdatedListener
) : BillingClientProvider {

    override var billingClient: BillingClient = BillingClient
        .newBuilder(context.applicationContext)
        .enablePendingPurchases()
        .setListener(updateListener)
        .build()

}