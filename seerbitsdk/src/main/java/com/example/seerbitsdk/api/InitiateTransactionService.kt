package com.example.seerbitsdk.api

import com.example.seerbitsdk.BuildConfig
import com.example.seerbitsdk.models.transfer.TransferDTO
import com.example.seerbitsdk.models.card.CardDTO
import com.example.seerbitsdk.models.card.CardResponse
import com.example.seerbitsdk.models.query.QueryTransactionResponse
import com.example.seerbitsdk.models.ussd.UssdDTO
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

interface InitiateTransactionService {

    @POST("initiates")
    suspend fun initiateCard(@Body cardDTO: CardDTO): Response<CardResponse>

    @POST("initiates")
    suspend fun initiateTransfer(@Body transferDTO: TransferDTO): Response<CardResponse>

    //This will be in use once we're on live
    @POST("initiates")
    suspend fun initiateUssd(@Body ussdDTO: UssdDTO): Response<CardResponse>

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


private val retrofit = Retrofit.Builder()
    .baseUrl("https://seerbitapi.com/checkout/")
    .client(okHttpClient.build())
    .addConverterFactory(GsonConverterFactory.create())
    .build()

object InitiateTransactionApiService {
    val retrofitService: InitiateTransactionService by lazy {
        retrofit.create(InitiateTransactionService::class.java)
    }
}
