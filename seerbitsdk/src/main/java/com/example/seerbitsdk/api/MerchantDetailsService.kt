package com.example.seerbitsdk.api

import com.example.seerbitsdk.BuildConfig
import com.example.seerbitsdk.component.PUBLIC_KEY
import com.example.seerbitsdk.models.CardBinResponse
import com.example.seerbitsdk.models.GetBanksResponse
import com.example.seerbitsdk.models.bankaccount.BankAccountDTO
import com.example.seerbitsdk.models.card.CardResponse
import com.example.seerbitsdk.models.home.MerchantDetailsResponse
import com.example.seerbitsdk.models.momo.MomoDTO
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
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Singleton
interface MerchantDetailsService {

    @GET("merchant/verify/")
    suspend fun merchantDetails(@Query("key") key : String = PUBLIC_KEY,
                                @Query("sbcp") sbcp : String = "uFWgBWF8OB56oCSJudCxKYqNm8Cttss4",
                                @Query("partner-id") partner_id : String = "1"
    ): Response<MerchantDetailsResponse>


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

private val publicKey = PUBLIC_KEY
const val baseUrl = "https://seerbitapi.com/"

private val retrofit = Retrofit.Builder()

    .baseUrl(baseUrl)
    .client(okHttpClient.build())
    .addConverterFactory(GsonConverterFactory.create())
    .build()


object MerchantServiceApi {
    val retrofitService: MerchantDetailsService by lazy {
        retrofit.create(MerchantDetailsService::class.java)
    }
}

