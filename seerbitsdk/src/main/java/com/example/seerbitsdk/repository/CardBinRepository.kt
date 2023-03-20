package com.example.seerbitsdk.repository

import com.example.seerbitsdk.api.InitiateTransactionServiceApi
import com.example.seerbitsdk.models.CardBinResponse
import retrofit2.Response

class CardBinRepository {
    suspend fun getCardBin(firstSixDigit : String): Response<CardBinResponse> {
        return InitiateTransactionServiceApi.retrofitService.getCardBin(firstSixDigit)
    }
}