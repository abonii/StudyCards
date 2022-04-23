package abm.co.studycards.domain

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

//class BillingUpdateListener @Inject constructor(
//
//) : PurchasesUpdatedListener {
//
//    private val _purchaseStateFlow = MutableSharedFlow<List<Purchase>?>(
//        extraBufferCapacity = 1,
//        onBufferOverflow = BufferOverflow.DROP_OLDEST
//    )
//    val purchaseStateFlow = _purchaseStateFlow.asSharedFlow()
//
//    override fun onPurchasesUpdated(
//        billingResult: BillingResult,
//        purchases: MutableList<Purchase>?
//    ) {
//        when (billingResult.responseCode) {
//            BillingClient.BillingResponseCode.OK -> {
//                purchases?.run { _purchaseStateFlow.tryEmit(this) }
//            }
//        }
//    }
//}