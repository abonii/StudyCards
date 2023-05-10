package abm.co.data.di

import abm.co.data.repository.AuthorizationRepositoryImpl
import abm.co.domain.repository.StoreRepository
import abm.co.data.repository.StoreRepositoryImpl
import abm.co.domain.repository.DictionaryRepository
import abm.co.data.repository.DictionaryRepositoryImpl
import abm.co.data.repository.FirebaseRepositoryImpl
import abm.co.data.repository.LanguagesRepositoryImpl
import abm.co.domain.repository.AuthorizationRepository
import abm.co.domain.repository.LanguagesRepository
import abm.co.domain.repository.ServerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindServerCloudRepository(repository: FirebaseRepositoryImpl): ServerRepository

    @Binds
    abstract fun bindAuthorizationRepository(repository: AuthorizationRepositoryImpl): AuthorizationRepository

    @Binds
    abstract fun bindDictionaryRepository(repository: DictionaryRepositoryImpl): DictionaryRepository

    @Singleton
    @Binds
    abstract fun bindStoreRepository(repository: StoreRepositoryImpl): StoreRepository

    @Binds
    abstract fun bindLanguagesRepository(repository: LanguagesRepositoryImpl): LanguagesRepository
}
