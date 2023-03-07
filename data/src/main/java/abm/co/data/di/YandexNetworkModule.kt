package abm.co.data.di

import abm.co.data.BuildConfig
import abm.co.data.model.qualifier.TypeEnum
import abm.co.data.model.qualifier.YandexNetwork
import abm.co.data.remote.YandexApiService
import abm.co.data.utils.BaseURLs.BASE_URL_YANDEX
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
    @YandexNetwork(TypeEnum.URL)
    fun provideYandexUrl() = BASE_URL_YANDEX

    @Singleton
    @Provides
    @YandexNetwork(TypeEnum.GSON)
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Singleton
    @Provides
    @YandexNetwork(TypeEnum.OKHTTP)
    fun provideOkHttpClient(): OkHttpClient =
        if (BuildConfig.DEBUG) { // debug ON
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
    @YandexNetwork(TypeEnum.RETROFIT)
    fun provideRetrofit(
        @YandexNetwork(TypeEnum.OKHTTP) okHttpClient: OkHttpClient,
        @YandexNetwork(TypeEnum.URL) baseURL: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    @YandexNetwork(TypeEnum.APISERVICE)
    fun provideYandexApiService(
        @YandexNetwork(TypeEnum.RETROFIT) retrofit: Retrofit
    ): YandexApiService = retrofit.create(YandexApiService::class.java)

}