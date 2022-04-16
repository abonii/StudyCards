package abm.co.studycards.data

import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.domain.BillingClientProvider
import abm.co.studycards.util.Constants.SUBSCRIPTIONS_PRODUCTS
import abm.co.studycards.util.connect
import abm.co.studycards.util.getProducts
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PricingRepositoryImpl @Inject constructor(
    private val billingClientProvider: BillingClientProvider,
    @ApplicationContext val context: Context,
    private val prefs: Prefs
) : PricingRepository {

    private var _purchaseStateFlow: MutableStateFlow<List<Purchase>?> = MutableStateFlow(
        null
    )
    override val purchaseStateFlow = _purchaseStateFlow.asStateFlow()

    override suspend fun setPurchases(purchaseType: String) {
        connectIfNeeded()
        getBillingClient().queryPurchasesAsync(purchaseType) { _, listener ->
            _purchaseStateFlow.value = listener
        }
    }

    override suspend fun getSubscriptions(): List<SkuDetails>? {
        val queryParamsBuilder = SkuDetailsParams.newBuilder().apply {
            setSkusList(SUBSCRIPTIONS_PRODUCTS)
            setType(BillingClient.SkuType.SUBS)
        }

        return getProducts(queryParamsBuilder.build())
    }

    private suspend fun acknowledgePurchase(purchase: Purchase): BillingResult? {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED || purchase.isAcknowledged)
            return null

        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        return getBillingClient().acknowledgePurchase(acknowledgePurchaseParams)
    }

    override suspend fun acknowledgePurchases(list: List<Purchase>?) {
        Toast.makeText(context, "purch: " + list.toString(), Toast.LENGTH_SHORT).show()
        list?.forEach {
            when(acknowledgePurchase(it)?.responseCode){
                BillingClient.BillingResponseCode.OK -> {
                    prefs.setIsPremium(true)
                }
            }
        }
    }

    override suspend fun getProducts(productDetailsParams: SkuDetailsParams): List<SkuDetails>? {
        return withContext(Dispatchers.IO) {
            val connectIfNeeded = connectIfNeeded()
            if (!connectIfNeeded)
                null
            else
                getBillingClient().getProducts(productDetailsParams)
        }
    }

    private suspend fun connectIfNeeded(): Boolean {
        return withContext(Dispatchers.IO) {
            getBillingClient().isReady || getBillingClient().connect() || billingClientProvider.reInitClient()
        }
    }

    override fun getBillingClient() = billingClientProvider.billingClient

}