package com.example.seerbitsdk.repository

import com.example.seerbitsdk.api.InitiateTransactionServiceApi
import com.example.seerbitsdk.api.MerchantDetailsService
import com.example.seerbitsdk.api.MerchantServiceApi
import com.example.seerbitsdk.models.home.MerchantDetailsResponse
import retrofit2.Response

import javax.inject.Singleton

@Singleton
class SeerMerchantDetailsRepository {


    suspend fun getMerchantDetails(pulicKey: String): Response<MerchantDetailsResponse> {
        return MerchantServiceApi.retrofitService.merchantDetails(pulicKey)
    }

}