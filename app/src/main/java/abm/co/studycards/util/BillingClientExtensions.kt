package abm.co.studycards.util

import abm.co.studycards.data.ConsumeProductResult
import com.android.billingclient.api.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

//suspend fun BillingClient.connect(): Boolean {
//    return suspendCancellableCoroutine { continuation ->
//        startConnection(object : BillingClientStateListener {
//            override fun onBillingSetupFinished(billingResult: BillingResult) {
//                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                    continuation.resume(true)
//                } else {
//                    continuation.resume(false)
//                }
//            }
//            override fun onBillingServiceDisconnected() {
//                continuation.resume(false)
//            }
//        })
//    }
//}

//suspend fun BillingClient.getProducts(params: SkuDetailsParams): List<SkuDetails>? {
//    return suspendCancellableCoroutine { continuation ->
//        querySkuDetailsAsync(params) { _, products ->
//            continuation.resume(products)
//        }
//    }
//}
//suspend fun BillingClient.consumeProduct(consumeParams: ConsumeParams): ConsumeProductResult {
//    return suspendCancellableCoroutine { continuation ->
//        consumeAsync(consumeParams) { billingResult, purchaseToken ->
//            continuation.resume(
//                ConsumeProductResult(
//                    billingResult = billingResult,
//                    purchaseToken = purchaseToken
//                )
//            )
//        }
//    }
//}