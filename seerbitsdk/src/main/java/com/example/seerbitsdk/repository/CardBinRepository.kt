package com.example.seerbitsdk.repository

import com.example.seerbitsdk.api.MerchantServiceApi
import com.example.seerbitsdk.models.CardBinResponse
import com.example.seerbitsdk.models.GetBanksResponse
import retrofit2.Response

class CardBinRepository {
    suspend fun getCardBin(firstSixDigit : String): Response<CardBinResponse> {
        return MerchantServiceApi.retrofitService.getCardBin(firstSixDigit)
    }
}