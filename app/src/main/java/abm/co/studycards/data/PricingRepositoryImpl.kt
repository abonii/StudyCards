package abm.co.studycards.data

import abm.co.studycards.data.pref.Prefs
import abm.co.studycards.domain.BillingClientProvider
import abm.co.studycards.util.Constants.SUBSCRIPTIONS_PRODUCTS
import abm.co.studycards.util.connect
import abm.co.studycards.util.getProducts
import android.content.Context
import android.widget.Toast
import com.android.billingclient.api.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PricingRepositoryImpl @Inject constructor(
    private val billingClientProvider: BillingClientProvider,
    @ApplicationContext val context: Context,
    private val prefs: Prefs,
    private val firebaseFunctions: FirebaseFunctions,
    private val firebaseAuth: FirebaseAuth
) : PricingRepository {

    private var _purchaseStateFlow: MutableStateFlow<List<Purchase>?> = MutableStateFlow(null)
    override val purchaseStateFlow = _purchaseStateFlow.asStateFlow()

    private var _verifySharedFlow: MutableSharedFlow<List<Purchase>?> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val verifySharedFlow = _verifySharedFlow.asSharedFlow()

    override suspend fun setPurchases(purchaseType: String) {
        val connectIfNeeded = connectIfNeeded()
        if (!connectIfNeeded) return
        getBillingClient().queryPurchasesAsync(purchaseType) { _, listener ->
            _purchaseStateFlow.value = listener
        }
    }

    override suspend fun getSubscriptions(): List<SkuDetails>? {
        val connectIfNeeded = connectIfNeeded()
        if (!connectIfNeeded)
            return null
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
        list?.forEach {
            when (acknowledgePurchase(it)?.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    prefs.setIsPremium(true)
                }
            }
        }
    }

    override suspend fun verifySubscriptions(list: List<Purchase>?) {
        list?.forEach {
            verifySubscription(it)
        }
    }

    private fun verifySubscription(purchase: Purchase) {
        Toast.makeText(context, "verifySubscription: $purchase", Toast.LENGTH_LONG).show()
        val data = mapOf(
            "sku_id" to BillingClient.SkuType.SUBS,
            "purchase_token" to purchase.purchaseToken,
            "package_name" to "abm.co.studycards",
            "user_id" to firebaseAuth.currentUser?.uid,
        )
        firebaseFunctions.getHttpsCallable("verifySubscription").call(data).continueWith { task ->
            try {
                (task.result?.data as HashMap<*, *>).let {
                    val verifySubscription = SubscriptionVerify(
                        status = it["status"] as Int,
                        message = it["message"] as String
                    )
                    Toast.makeText(
                        context,
                        "called verify from server: ${verifySubscription.message}",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    if (verifySubscription.status == 200)
                        _verifySharedFlow.tryEmit(listOf(purchase))
                }
            } catch (e: Exception) {
                Toast.makeText(context, "getHttpsCall error: " + e.message, Toast.LENGTH_SHORT)
                    .show()
                null
            }
        }
    }

    override suspend fun getProducts(productDetailsParams: SkuDetailsParams): List<SkuDetails>? {
        return withContext(Dispatchers.IO) {
            val connectIfNeeded = connectIfNeeded()
            if (!connectIfNeeded)
                null
            else getBillingClient().getProducts(productDetailsParams)
        }
    }

    private suspend fun connectIfNeeded(): Boolean {
        return withContext(Dispatchers.IO) {
            getBillingClient().isReady || getBillingClient().connect()
        }
    }

    override fun getBillingClient() = billingClientProvider.billingClient

}