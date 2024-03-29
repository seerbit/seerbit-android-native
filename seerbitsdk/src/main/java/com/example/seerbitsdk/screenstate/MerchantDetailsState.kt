package com.example.seerbitsdk.screenstate

import com.example.seerbitsdk.models.CardBinResponse
import com.example.seerbitsdk.models.GetBanksResponse
import com.example.seerbitsdk.models.card.CardResponse
import com.example.seerbitsdk.models.fee.FeeResponse
import com.example.seerbitsdk.models.home.MerchantDetailsResponse
import com.example.seerbitsdk.models.momo.MomoNetworkResponse
import com.example.seerbitsdk.models.momo.MomoNetworkResponseItem
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

data class MomoNetworkState(
    val data: List<MomoNetworkResponseItem>? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null

)

data class CardBinState(
    val data: CardBinResponse? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null

)


data class FeeState(
    val data: FeeResponse? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null

)


