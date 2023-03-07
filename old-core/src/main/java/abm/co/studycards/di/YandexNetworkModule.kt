package abm.co.studycards.di

import abm.co.studycards.data.network.YandexApiService
import abm.co.studycards.di.qualifier.TypeEnum.*
import abm.co.studycards.di.qualifier.YandexNetwork
import abm.co.studycards.util.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class YandexNetworkModule {
    @Singleton
    @Provides
    @YandexNetwork(URL)
    fun provideYandexUrl() = Constants.BASE_URL_YANDEX

    @Singleton
    @Provides
    @YandexNetwork(GSON)
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Singleton
    @Provides
    @YandexNetwork(OKHTTP)
    fun provideOkHttpClient(): OkHttpClient =
        if (Constants.DEBUG) { // debug ON
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC
            OkHttpClient.Builder()
                .addInterceptor(logger)
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .build()
        } else // debug OFF
            OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .build()

    @Singleton
    @Provides
    @YandexNetwork(RETROFIT)
    fun provideRetrofit(
        @YandexNetwork(OKHTTP) okHttpClient: OkHttpClient,
        @YandexNetwork(URL) baseURL: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    @YandexNetwork(APISERVICE)
    fun provideYandexApiService(
        @YandexNetwork(RETROFIT) retrofit: Retrofit
    ): YandexApiService = retrofit.create(YandexApiService::class.java)

}