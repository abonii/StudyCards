package abm.co.data.di

import com.mocklets.pluto.PlutoInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PlutoModule {

    @Singleton
    @Provides
    fun providePlutoInterceptor(): PlutoInterceptor = PlutoInterceptor()
}
