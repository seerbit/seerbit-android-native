package com.example.seerbitsdk.repository

import com.example.seerbitsdk.api.FeeApiService
import com.example.seerbitsdk.api.InitiateTransactionApiService
import com.example.seerbitsdk.api.InitiateTransactionServiceApi
import com.example.seerbitsdk.models.bankaccount.BankAccountDTO
import com.example.seerbitsdk.models.card.CardDTO
import com.example.seerbitsdk.models.card.CardResponse
import com.example.seerbitsdk.models.fee.FeeDto
import com.example.seerbitsdk.models.fee.FeeResponse
import com.example.seerbitsdk.models.momo.MomoDTO
import com.example.seerbitsdk.models.query.QueryTransactionResponse
import com.example.seerbitsdk.models.transfer.TransferDTO
import com.example.seerbitsdk.models.ussd.UssdDTO
import retrofit2.Response

class InitiateTransactionRepository {

    suspend fun initiateCard(cardDTO: CardDTO): Response<CardResponse> {
        return InitiateTransactionApiService.retrofitService.initiateCard(cardDTO)
    }

    suspend fun initiateUssd(ussdDTO: UssdDTO): Response<CardResponse> {
        return InitiateTransactionServiceApi.retrofitService.initiateUssd(ussdDTO)
    }

    suspend fun initiateTransfer(transferDTO: TransferDTO): Response<CardResponse> {
        return InitiateTransactionServiceApi.retrofitService.initiateTransfer(transferDTO)
    }

    suspend fun initiateBankAccountMode(bankAccountDTO: BankAccountDTO): Response<CardResponse> {
        return InitiateTransactionServiceApi.retrofitService.initiateBankAccountMode(bankAccountDTO)
    }
    suspend fun initiateMOMO(momoDTO: MomoDTO): Response<CardResponse> {
        return InitiateTransactionServiceApi.retrofitService.initiateMomo(momoDTO)
    }

    //todo move this later
    suspend fun queryTransaction(paymentReference: String): Response<QueryTransactionResponse> {
        return InitiateTransactionServiceApi.retrofitService.queryTransaction(paymentReference)
    }

    //todo move this later
    suspend fun getFee(feeDto: FeeDto): Response<FeeResponse> {
        return FeeApiService.retrofitService.getFee(feeDto)
    }
}