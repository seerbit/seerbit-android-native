package com.example.seerbitsdk.api

import com.example.seerbitsdk.BuildConfig
import com.example.seerbitsdk.models.CardBinResponse
import com.example.seerbitsdk.models.GetBanksResponse
import com.example.seerbitsdk.models.bankaccount.BankAccountDTO
import com.example.seerbitsdk.models.card.CardResponse
import com.example.seerbitsdk.models.home.MerchantDetailsResponse
import com.example.seerbitsdk.models.momo.MomoDTO
import com.example.seerbitsdk.models.momo.MomoNetworkResponse
import com.example.seerbitsdk.models.momo.MomoNetworkResponseItem
import com.example.seerbitsdk.models.query.QueryTransactionResponse
import com.example.seerbitsdk.models.transfer.TransferDTO
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
import javax.inject.Singleton

@Singleton
interface SeerBitService {

    @GET("merchant/verify/?key=SBPUBK_WWEQK6UVR1PNZEVVUOBNIQHEIEIM1HJC&sbcp=uFWgBWF8OB56oCSJudCxKYqNm8Cttss4&partner-id=1")
    suspend fun merchantDetails(): Response<MerchantDetailsResponse>

    @GET("checkout/query/{paymentReference}")
    suspend fun queryTransaction(@Path("paymentReference") paymentReference: String)
            : Response<QueryTransactionResponse>

    // using this here because the url points to live
    @POST("checkout/initiates")
    suspend fun initiateUssd(@Body ussdDTO: UssdDTO): Response<CardResponse>

    // using this here because the url points to live
    @POST("checkout/initiates")
    suspend fun initiateTransfer(@Body transferDTO: TransferDTO): Response<CardResponse>

    @POST("checkout/initiates")
    suspend fun initiateMomo(@Body transferDTO: TransferDTO): Response<CardResponse>

    // using this here because the url points to live
    @POST("checkout/initiates")
    suspend fun initiateBankAccountMode(@Body bankAccountDTO: BankAccountDTO): Response<CardResponse>

    // using this here because the url points to live
    @POST("checkout/initiates")
    suspend fun initiateMomo(@Body momoDTO: MomoDTO): Response<CardResponse>

    @GET("checkout/banks")
    suspend fun getBanks(): Response<GetBanksResponse>

    @GET("tranmgt/networks/GH/00000103")
    suspend fun getMomoNetworks(): Response<List<MomoNetworkResponseItem>>

    @GET("checkout/bin/{firstSixDigit}")
    suspend fun getCardBin(@Path("firstSixDigit") firstSixDigit: String): Response<CardBinResponse>


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
    .connectTimeout(60, TimeUnit.SECONDS)
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
    .baseUrl("https://seerbitapi.com/")
    .client(okHttpClient.build())
    .addConverterFactory(GsonConverterFactory.create())
    .build()


object MerchantServiceApi {
    val retrofitService: SeerBitService by lazy {
        retrofit.create(SeerBitService::class.java)
    }
}
