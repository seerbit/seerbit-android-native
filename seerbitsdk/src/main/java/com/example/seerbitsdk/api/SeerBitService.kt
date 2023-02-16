package com.example.seerbitsdk.api

import com.example.seerbitsdk.BuildConfig
import com.example.seerbitsdk.models.home.MerchantDetailsResponse
import com.example.seerbitsdk.models.query.QueryTransactionResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Singleton
interface SeerBitService {

    @GET("merchant/clear/SBTESTPUBK_t4G16GCA1O51AV0Va3PPretaisXubSw1")
    suspend fun merchantDetails(): Response<MerchantDetailsResponse>

    @GET("query/{paymentReference}")
    suspend fun queryTransaction(@Path("paymentReference") paymentReference: String)
            : Response<QueryTransactionResponse>
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

private val retrofit = Retrofit.Builder()
    .baseUrl("https://seerbitapi.com/checkout/")
    .client(okHttpClient.build())
    .addConverterFactory(GsonConverterFactory.create())
    .build()

object SeerBitApi {
    val retrofitService: SeerBitService by lazy {
        retrofit.create(SeerBitService::class.java)
    }
}
