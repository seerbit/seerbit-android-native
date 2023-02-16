package com.example.seerbitsdk.repository

import com.example.seerbitsdk.api.InitiateApiService
import com.example.seerbitsdk.api.SeerBitApi
import com.example.seerbitsdk.models.card.CardDTO
import com.example.seerbitsdk.models.card.CardResponse
import com.example.seerbitsdk.models.home.MerchantDetailsResponse
import com.example.seerbitsdk.models.query.QueryTransactionResponse
import com.example.seerbitsdk.models.transfer.TransferDTO
import com.example.seerbitsdk.repository.use_cases.InitiateUseCase
import retrofit2.Call
import retrofit2.Response

class InitiateTransactionRepository {

    suspend fun initiateCard(cardDTO: CardDTO): Response<CardResponse> {
        return InitiateApiService.retrofitService.initiateCard(cardDTO)
    }
    suspend fun initiateTransfer(transferDTO: TransferDTO): Response<CardResponse> {
        return InitiateApiService.retrofitService.initiateTransfer(transferDTO)
    }

    suspend fun queryTransaction(paymentReference: String): Response<QueryTransactionResponse> {
        return SeerBitApi.retrofitService.queryTransaction(paymentReference)
    }
}