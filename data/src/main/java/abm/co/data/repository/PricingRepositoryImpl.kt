package abm.co.data.repository

import abm.co.data.model.purchase.PurchaseVerifyDTO
import abm.co.data.utils.BaseURLs.AN_APP_SKUS
import abm.co.data.utils.BaseURLs.PRODUCT_TYPE
import abm.co.domain.repository.PricingRepository
import android.content.Context
import android.widget.Toast
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val VERIFY_PRODUCT_FUN = "verifyProduct"

@Singleton
class PricingRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val firebaseFunctions: FirebaseFunctions,
    private val coroutineScope: CoroutineScope,
    private val firebaseAuth: FirebaseAuth
) : PricingRepository, BillingClientStateListener, PurchasesUpdatedListener {

    private val _skusStateFlow = MutableStateFlow<List<SkuDetails>>(emptyList())
    private val skusStateFlow: StateFlow<List<SkuDetails>> = _skusStateFlow.asStateFlow()

    private var _billingClient: BillingClient = BillingClient
        .newBuilder(context.applicationContext)
        .enablePendingPurchases()
        .setListener(this)
        .build()

    val billingClient get() = _billingClient

    fun startConnection() {
        coroutineScope.launch(Dispatchers.IO) {
            _billingClient.startConnection(this@PricingRepositoryImpl)
        }
    }

    private fun listenForPurchase() {
        _billingClient.queryPurchasesAsync(PRODUCT_TYPE) { result, purchases ->
            coroutineScope.launch(Dispatchers.IO) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
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
                    val verifyPurchase = PurchaseVerifyDTO(
                        status = it["status"] as Int,
                        purchaseState = it["purchaseState"] as Int?
                    )
                    if (verifyPurchase.status == 200 &&
                        verifyPurchase.purchaseState != Purchase.PurchaseState.PENDING
                    ) {
                        consumeProduct(purchase)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun consumeProduct(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        _billingClient.consumeAsync(consumeParams) { _, _ -> }
    }

    private fun getProducts(params: SkuDetailsParams) {
        _billingClient.querySkuDetailsAsync(params) { _, products ->
            if (products != null) {
                coroutineScope.launch(Dispatchers.IO) {
                    products.sortBy { it.price }
                    _skusStateFlow.emit(products)
                }
            }
        }
    }

    override fun onBillingServiceDisconnected() {
        startConnection()
    }

    override fun onBillingSetupFinished(response: BillingResult) {
        coroutineScope.launch(Dispatchers.IO) {
            when (response.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    listenForPurchase()
                    querySkuDetailsAsync()
                }
            }
        }
    }

    override fun onPurchasesUpdated(res: BillingResult, p1: MutableList<Purchase>?) {
        coroutineScope.launch(Dispatchers.IO) {
            if (res.responseCode == BillingClient.BillingResponseCode.OK) {
                verifySubscriptions(p1)
            }
        }
    }

}