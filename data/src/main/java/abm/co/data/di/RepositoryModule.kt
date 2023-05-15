package abm.co.data.di

import abm.co.data.repository.AuthorizationRepositoryImpl
import abm.co.data.repository.ConfigRepositoryImpl
import abm.co.data.repository.DictionaryRepositoryImpl
import abm.co.data.repository.FirebaseRepositoryImpl
import abm.co.data.repository.LanguagesRepositoryImpl
import abm.co.data.repository.StoreRepositoryImpl
import abm.co.domain.repository.AuthorizationRepository
import abm.co.domain.repository.ConfigRepository
import abm.co.domain.repository.DictionaryRepository
import abm.co.domain.repository.LanguagesRepository
import abm.co.domain.repository.ServerRepository
import abm.co.domain.repository.StoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositorySingletonModule {

    @Binds
    abstract fun bindStoreRepository(repository: StoreRepositoryImpl): StoreRepository

    @Binds
    abstract fun bindLanguagesRepository(repository: LanguagesRepositoryImpl): LanguagesRepository

    @Binds
    abstract fun bindConfigRepository(repository: ConfigRepositoryImpl): ConfigRepository
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindDictionaryRepository(repository: DictionaryRepositoryImpl): DictionaryRepository

    @Binds
    abstract fun bindServerCloudRepository(repository: FirebaseRepositoryImpl): ServerRepository

    @Binds
    abstract fun bindAuthorizationRepository(repository: AuthorizationRepositoryImpl): AuthorizationRepository
}
