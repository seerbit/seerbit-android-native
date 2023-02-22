package com.example.seerbitsdk.screenstate

import com.example.seerbitsdk.models.GetBanksResponse
import com.example.seerbitsdk.models.card.CardResponse
import com.example.seerbitsdk.models.home.MerchantDetailsResponse
import com.example.seerbitsdk.models.query.QueryTransactionResponse

data class MerchantDetailsState(
    val data: MerchantDetailsResponse? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null

)

data class QueryTransactionState(
    val data: QueryTransactionResponse? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null

)

data class InitiateTransactionState(
    val data: CardResponse? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null

)


data class OTPState(
    val data: CardResponse? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null

)


data class AvailableBanksState(
    val data: GetBanksResponse? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null

)
