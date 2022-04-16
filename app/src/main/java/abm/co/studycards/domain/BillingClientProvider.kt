package abm.co.studycards.domain

import abm.co.studycards.util.connect
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingClientProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val updateListener: PurchasesUpdatedListener
) {
    lateinit var billingClient: BillingClient

    init {
        initClient()
    }

    private fun initClient() {
        billingClient = BillingClient
            .newBuilder(context)
            .enablePendingPurchases()
            .setListener(updateListener)
            .build()
    }

    suspend fun reInitClient(): Boolean {
        initClient()
        return billingClient.isReady || billingClient.connect()
    }
}