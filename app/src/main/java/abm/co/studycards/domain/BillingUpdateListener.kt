package abm.co.studycards.domain

import android.content.Context
import android.widget.Toast
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingUpdateListener @Inject constructor(
    @ApplicationContext val context: Context
) : PurchasesUpdatedListener {

    private val _purchaseUpdateLiveData = MutableSharedFlow<List<Purchase>?>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val purchaseUpdateLiveData = _purchaseUpdateLiveData.asSharedFlow()

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.run { _purchaseUpdateLiveData.tryEmit(this) }
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                purchases?.run { _purchaseUpdateLiveData.tryEmit(this) }
            }
        }
    }
}