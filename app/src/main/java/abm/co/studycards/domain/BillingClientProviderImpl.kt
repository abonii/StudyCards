package abm.co.studycards.domain

import android.content.Context
import android.widget.Toast
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

class BillingClientProviderImpl @Inject constructor(
    override var billingClient: String
) : BillingClientProvider {



}