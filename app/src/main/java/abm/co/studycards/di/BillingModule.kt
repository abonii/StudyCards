package abm.co.studycards.di

import abm.co.studycards.domain.BillingClientProvider
import abm.co.studycards.domain.BillingClientProviderImpl
import abm.co.studycards.domain.BillingUpdateListener
import com.android.billingclient.api.PurchasesUpdatedListener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BillingModule {

    @Singleton
    @Provides
    fun provideBillingUpdateListener(impl: BillingUpdateListener): PurchasesUpdatedListener = impl

    @Singleton
    @Provides
    fun provideBillingProvider(impl: BillingClientProviderImpl): BillingClientProvider = impl

}