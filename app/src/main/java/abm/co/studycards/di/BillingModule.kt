package abm.co.studycards.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class BillingModule {

//    @Singleton
//    @Provides
//    fun provideBillingUpdateListener(impl: BillingUpdateListener): PurchasesUpdatedListener = impl

//    @Singleton
//    @Provides
//    fun provideBillingProvider(impl: BillingClientProviderImpl): BillingClientProvider = impl

}