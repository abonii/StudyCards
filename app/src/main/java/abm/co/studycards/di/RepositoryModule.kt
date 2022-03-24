package abm.co.studycards.di

import abm.co.studycards.data.repository.DictionaryRepository
import abm.co.studycards.data.repository.DictionaryRepositoryImp
import abm.co.studycards.data.repository.FirebaseRepositoryImp
import abm.co.studycards.data.repository.ServerCloudRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindServerCloudRepository(repository: FirebaseRepositoryImp): ServerCloudRepository

    @Binds
    abstract fun bindDictionaryRepository(repository: DictionaryRepositoryImp): DictionaryRepository
}