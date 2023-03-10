package abm.co.data.di

import abm.co.domain.repository.PricingRepository
import abm.co.data.repository.PricingRepositoryImpl
import abm.co.domain.repository.DictionaryRepository
import abm.co.data.repository.DictionaryRepositoryImp
import abm.co.data.repository.FirebaseRepositoryImp
import abm.co.domain.repository.ServerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindServerCloudRepository(repository: FirebaseRepositoryImp): ServerRepository

    @Binds
    abstract fun bindDictionaryRepository(repository: DictionaryRepositoryImp): DictionaryRepository

    @Binds
    abstract fun bindBillingRepository(repository: PricingRepositoryImpl): PricingRepository
}
