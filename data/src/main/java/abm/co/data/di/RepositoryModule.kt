package abm.co.data.di

import abm.co.data.repository.AuthorizationRepositoryImpl
import abm.co.domain.repository.PricingRepository
import abm.co.data.repository.PricingRepositoryImpl
import abm.co.domain.repository.DictionaryRepository
import abm.co.data.repository.DictionaryRepositoryImpl
import abm.co.data.repository.FirebaseRepositoryImpl
import abm.co.domain.repository.AuthorizationRepository
import abm.co.domain.repository.ServerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindServerCloudRepository(repository: FirebaseRepositoryImpl): ServerRepository

    @Binds
    abstract fun bindAuthorizationRepository(repository: AuthorizationRepositoryImpl): AuthorizationRepository

    @Binds
    abstract fun bindDictionaryRepository(repository: DictionaryRepositoryImpl): DictionaryRepository

    @Binds
    abstract fun bindBillingRepository(repository: PricingRepositoryImpl): PricingRepository
}
