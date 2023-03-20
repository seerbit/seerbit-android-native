package com.example.seerbitsdk.api

import com.example.seerbitsdk.BuildConfig
import com.example.seerbitsdk.models.fee.FeeDto
import com.example.seerbitsdk.models.fee.FeeResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit


interface FeeService {

    @POST("otp")
    suspend fun getFee(@Body feeDto: FeeDto)
            : Response<FeeResponse>
}


private val okHttpClient = OkHttpClient.Builder()
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
    .baseUrl("https://gex2635n2p5f6zvjckeqygbcha0cmlnp.lambda-url.eu-west-2.on.aws/")
    .client(okHttpClient.build())
    .addConverterFactory(GsonConverterFactory.create())
    .build()

object FeeApiService {
    val retrofitService: FeeService by lazy {
        retrofit.create(FeeService::class.java)
    }
}
