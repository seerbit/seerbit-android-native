package com.example.seerbitsdk.api

import com.example.seerbitsdk.BuildConfig
import com.example.seerbitsdk.models.CardOTPDTO
import com.example.seerbitsdk.models.card.CardResponse
import com.example.seerbitsdk.models.home.MerchantDetailsResponse
import com.example.seerbitsdk.models.query.QueryTransactionResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface OTPService {

    @POST("otp")
    suspend fun sendOtp(@Body sendOTPDTO: CardOTPDTO)
            : Response<CardResponse>
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



private val retrofit = Retrofit.Builder()
    .baseUrl("https://seerbitapi.com/sandbox/")
    .client(okHttpClient.build())
    .addConverterFactory(GsonConverterFactory.create())
    .build()

object OtpApiService {
    val retrofitService: OTPService by lazy {
        retrofit.create(OTPService::class.java)
    }
}
