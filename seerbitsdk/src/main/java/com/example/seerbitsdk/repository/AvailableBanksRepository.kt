package com.example.seerbitsdk.repository

import com.example.seerbitsdk.api.InitiateTransactionServiceApi
import com.example.seerbitsdk.models.GetBanksResponse
import com.example.seerbitsdk.models.momo.MomoNetworkResponseItem
import retrofit2.Response

class AvailableBanksRepository {

    suspend fun getBanks(): Response<GetBanksResponse> {
        return InitiateTransactionServiceApi.retrofitService.getBanks()
    }

    suspend fun getMomoNetworks(): Response<List<MomoNetworkResponseItem>> {
        return InitiateTransactionServiceApi.retrofitService.getMomoNetworks()
    }


}