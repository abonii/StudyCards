package abm.co.studycards.data

import abm.co.studycards.util.Constants.AN_APP_SKUS
import abm.co.studycards.util.Constants.PRODUCT_TYPE
import abm.co.studycards.util.Constants.TAG
import abm.co.studycards.util.Constants.VERIFY_PRODUCT_FUN
import abm.co.studycards.util.core.App
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PricingRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val firebaseFunctions: FirebaseFunctions,
    private val firebaseAuth: FirebaseAuth,
    private val coroutineScope: CoroutineScope,
) : PricingRepository, BillingClientStateListener, PurchasesUpdatedListener {

    private val _skusLiveData = MutableStateFlow<List<SkuDetails>>(emptyList())
    override val skusStateFlow: StateFlow<List<SkuDetails>> = _skusLiveData.asStateFlow()

    override var billingClient: BillingClient = BillingClient
        .newBuilder(App.instance)
        .enablePendingPurchases()
        .setListener(this)
        .build()

    init {
        billingClient.startConnection(this)
    }

    private fun listenForPurchase() {
        billingClient.queryPurchasesAsync(PRODUCT_TYPE) { result, purchases ->
//            Log.i(TAG, "listen: " + result.responseCode.toString() + "-" + purchases)
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                coroutineScope.launch {
                    verifySubscriptions(purchases)
                }
            }
        }
    }

    private fun querySkuDetailsAsync() {
        val queryParamsBuilder = SkuDetailsParams.newBuilder().apply {
            setSkusList(AN_APP_SKUS)
            setType(PRODUCT_TYPE)
        }
        getProducts(queryParamsBuilder.build())
    }

    private fun verifySubscriptions(list: List<Purchase>?) {
        list?.forEach {
            verifyProduct(it)
        }
    }

    private fun verifyProduct(purchase: Purchase) {
        val data = mapOf(
            "sku_id" to purchase.skus.getOrNull(0),
            "purchase_token" to purchase.purchaseToken,
            "package_name" to purchase.packageName,
            "user_id" to (firebaseAuth.currentUser?.uid ?: "000000"),
        )
        firebaseFunctions.getHttpsCallable(VERIFY_PRODUCT_FUN).call(data).continueWith { task ->
            try {
                (task.result?.data as HashMap<*, *>).let {
                    val verifyPurchase = PurchaseVerify(
                        status = it["status"] as Int,
                        purchaseState = it["purchaseState"] as Int?
                    )
//                    Log.i(TAG, "verifySubscription: ${verifyPurchase.purchaseState}")
                    if (verifyPurchase.status == 200 &&
                        verifyPurchase.purchaseState != Purchase.PurchaseState.PENDING
                    ) {
                        Log.i(TAG, "verified: $verifyPurchase")
                        consumeProduct(purchase)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
//                Log.e(TAG, "verifySubscription: " + e.message)
                null
            }
        }
    }

    private fun consumeProduct(purchase: Purchase) {
        Log.i(TAG, "consumeProduct: $purchase")
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.consumeAsync(consumeParams) { _, _ -> }
    }

    private fun getProducts(params: SkuDetailsParams) {
        billingClient.querySkuDetailsAsync(params) { _, products ->
            if (products != null) {
                coroutineScope.launch {
                    products.sortBy { it.price }
                    _skusLiveData.emit(products)
                }
            }
        }
    }

    override fun onBillingServiceDisconnected() {
        billingClient.startConnection(this@PricingRepositoryImpl)
    }

    override fun onBillingSetupFinished(response: BillingResult) {
        listenForPurchase()
        when (response.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                coroutineScope.launch {
                    querySkuDetailsAsync()
                }
            }
        }
    }

    override fun onPurchasesUpdated(res: BillingResult, p1: MutableList<Purchase>?) {
        if (res.responseCode == BillingClient.BillingResponseCode.OK) {
            coroutineScope.launch {
                verifySubscriptions(p1)
            }
        }
    }

}