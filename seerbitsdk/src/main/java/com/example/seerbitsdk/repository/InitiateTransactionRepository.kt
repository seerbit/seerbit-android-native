package com.example.seerbitsdk.repository

import com.example.seerbitsdk.api.InitiateTransactionApiService
import com.example.seerbitsdk.api.MerchantServiceApi
import com.example.seerbitsdk.models.card.CardDTO
import com.example.seerbitsdk.models.card.CardResponse
import com.example.seerbitsdk.models.query.QueryTransactionResponse
import com.example.seerbitsdk.models.transfer.TransferDTO
import com.example.seerbitsdk.models.ussd.UssdDTO
import retrofit2.Response

class InitiateTransactionRepository {

    suspend fun initiateCard(cardDTO: CardDTO): Response<CardResponse> {
        return InitiateTransactionApiService.retrofitService.initiateCard(cardDTO)
    }

    suspend fun initiateUssd(ussdDTO: UssdDTO): Response<CardResponse> {
        return MerchantServiceApi.retrofitService.initiateUssd(ussdDTO)
    }

    suspend fun initiateTransfer(transferDTO: TransferDTO): Response<CardResponse> {
        return MerchantServiceApi.retrofitService.initiateTransfer(transferDTO)
    }


    //todo move this later
    suspend fun queryTransaction(paymentReference: String): Response<QueryTransactionResponse> {
        return MerchantServiceApi.retrofitService.queryTransaction(paymentReference)
    }
}