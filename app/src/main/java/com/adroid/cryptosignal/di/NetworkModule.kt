package com.adroid.cryptosignal.di

import com.adroid.cryptosignal.data.remote.BtcTurkApiService
import com.adroid.cryptosignal.data.remote.BtcTurkGraphApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BtcTurkRestBaseUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BtcTurkGraphBaseUrl

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val REST_BASE_URL = "https://api.btcturk.com/"
    private const val GRAPH_BASE_URL = "https://graph-api.btcturk.com/"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .pingInterval(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    @BtcTurkRestBaseUrl
    fun provideRestRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(REST_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    @BtcTurkGraphBaseUrl
    fun provideGraphRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(GRAPH_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideBtcTurkApiService(@BtcTurkRestBaseUrl retrofit: Retrofit): BtcTurkApiService =
        retrofit.create(BtcTurkApiService::class.java)

    @Provides
    @Singleton
    fun provideBtcTurkGraphApiService(@BtcTurkGraphBaseUrl retrofit: Retrofit): BtcTurkGraphApiService =
        retrofit.create(BtcTurkGraphApiService::class.java)
}
