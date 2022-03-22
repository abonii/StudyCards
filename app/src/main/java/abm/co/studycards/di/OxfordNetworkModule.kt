package abm.co.studycards.di

import abm.co.studycards.data.network.oxford.OxfordApiService
import abm.co.studycards.data.network.oxford.OxfordInterceptor
import abm.co.studycards.di.qualifier.OxfordNetwork
import abm.co.studycards.di.qualifier.TypeEnum.*
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

@Module
@InstallIn(SingletonComponent::class)
class OxfordNetworkModule {
    @Provides
    @OxfordNetwork(URL)
    fun provideOxfordUrl() = Constants.BASE_URL_OXFORD

    @Provides
    @OxfordNetwork(GSON)
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Provides
    @OxfordNetwork(OKHTTP)
    fun provideOkHttpClient(): OkHttpClient =
        if (Constants.DEBUG) { // debug ON
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY
            OkHttpClient.Builder()
                .addInterceptor(logger)
                .addInterceptor(OxfordInterceptor())
                .readTimeout(100, TimeUnit.SECONDS)
                .connectTimeout(100, TimeUnit.SECONDS)
                .build()
        } else // debug OFF
            OkHttpClient.Builder()
                .readTimeout(100, TimeUnit.SECONDS)
                .connectTimeout(100, TimeUnit.SECONDS)
                .build()

    @Provides
    @OxfordNetwork(RETROFIT)
    fun provideRetrofit(
        @OxfordNetwork(OKHTTP) okHttpClient: OkHttpClient,
        @OxfordNetwork(URL) BaseURL: String
    ):
            Retrofit = Retrofit.Builder()
        .baseUrl(BaseURL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @OxfordNetwork(APISERVICE)
    fun provideOxfordApiService(
        @OxfordNetwork(RETROFIT) retrofit: Retrofit
    ): OxfordApiService = retrofit.create(OxfordApiService::class.java)

}
