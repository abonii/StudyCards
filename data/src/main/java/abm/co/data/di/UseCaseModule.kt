package abm.co.data.di

import abm.co.domain.repository.AuthorizationRepository
import abm.co.domain.repository.DictionaryRepository
import abm.co.domain.repository.LanguagesRepository
import abm.co.domain.repository.ServerRepository
import abm.co.domain.usecase.GetWordInfoUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelUseCaseModule {

    @Provides
    fun provideGetWordInfoUseCase(
        languagesRepository: LanguagesRepository,
        serverRepository: ServerRepository,
        authorizationRepository: AuthorizationRepository,
        dictionaryRepository: DictionaryRepository
    ): GetWordInfoUseCase {
        return GetWordInfoUseCase(
            languagesRepository = languagesRepository,
            serverRepository = serverRepository,
            authorizationRepository = authorizationRepository,
            dictionaryRepository = dictionaryRepository
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object SingletonUseCaseModule {}
