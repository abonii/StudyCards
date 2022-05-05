package abm.co.studycards.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application
    }

//    @Singleton
//    @Provides
//    fun provideGlideInstance(@ApplicationContext context: Context) =
//        Glide.with(context).setDefaultRequestOptions(
//            RequestOptions()
//                .placeholder(R.drawable.ic_image)
//                .error(R.drawable.ic_image)
//        )
}