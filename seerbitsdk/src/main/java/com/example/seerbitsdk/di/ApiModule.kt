package com.example.seerbitsdk.di

import com.example.seerbitsdk.BuildConfig
import com.example.seerbitsdk.api.SeerBitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {

    @Provides
    @Singleton
    fun provideSeerBitSDKService(): SeerBitService {
        return Retrofit.Builder()
            .client(okHttpClient.build())
            .baseUrl(Baseurl.baseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SeerBitService::class.java)
    }


    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain: Interceptor.Chain ->
            val token = "68826aea9005de7812429b7983838b06e2c7fffbb3d9487f33bc51c943a3a499"
            var request = chain.request()
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(request)
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(logger())


    private fun logger(): HttpLoggingInterceptor {
        val logger = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG) { // add loggerInterceptor only when in debug
            logger.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else logger.setLevel(HttpLoggingInterceptor.Level.BODY)
        return logger
    }

    object Baseurl {

        fun baseUrl(): String {
            return if (BuildConfig.DEBUG) SEER_BIT_BASE_URL_TEST
            else SEER_BIT_BASE_LIVE_URL
        }

        private const val SEER_BIT_BASE_LIVE_URL = "https://seerbitapi.com/checkout"
        private const val SEER_BIT_BASE_URL_TEST = "https://seerbitapi.com/sandbox/"
    }
}