package com.example.seerbitsdk.repository

import com.example.seerbitsdk.api.SeerBitApi
import com.example.seerbitsdk.models.home.MerchantDetailsResponse
import com.example.seerbitsdk.models.query.QueryTransactionResponse
import retrofit2.Response

import javax.inject.Singleton

@Singleton
class SeerMerchantDetailsRepository {


    suspend fun getMerchantDetails(): Response<MerchantDetailsResponse> {
        return SeerBitApi.retrofitService.merchantDetails()
    }

}