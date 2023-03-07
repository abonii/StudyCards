package abm.co.data.di

import abm.co.data.BuildConfig
import abm.co.data.model.qualifier.OxfordNetwork
import abm.co.data.model.qualifier.TypeEnum
import abm.co.data.remote.OxfordApiService
import abm.co.data.utils.BaseURLs.BASE_URL_OXFORD
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
class OxfordNetworkModule {

    @Singleton
    @Provides
    @OxfordNetwork(TypeEnum.URL)
    fun provideOxfordUrl() = BASE_URL_OXFORD

    @Singleton
    @Provides
    @OxfordNetwork(TypeEnum.GSON)
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Singleton
    @Provides
    @OxfordNetwork(TypeEnum.OKHTTP)
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
    @OxfordNetwork(TypeEnum.RETROFIT)
    fun provideRetrofit(
        @OxfordNetwork(TypeEnum.OKHTTP) okHttpClient: OkHttpClient,
        @OxfordNetwork(TypeEnum.URL) BaseURL: String
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BaseURL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Singleton
    @Provides
    @OxfordNetwork(TypeEnum.APISERVICE)
    fun provideOxfordApiService(
        @OxfordNetwork(TypeEnum.RETROFIT) retrofit: Retrofit
    ): OxfordApiService = retrofit.create(OxfordApiService::class.java)

}
