package com.example.seerbitsdk.repository

import com.example.seerbitsdk.api.MerchantServiceApi
import com.example.seerbitsdk.models.GetBanksResponse
import com.example.seerbitsdk.models.bankaccount.BankAccountDTO
import com.example.seerbitsdk.models.home.MerchantDetailsResponse
import com.example.seerbitsdk.models.momo.MomoNetworkResponse
import retrofit2.Response

class AvailableBanksRepository {

    suspend fun getBanks(): Response<GetBanksResponse> {
        return MerchantServiceApi.retrofitService.getBanks()
    }

    suspend fun getMomoNetworks(): Response<MomoNetworkResponse> {
        return MerchantServiceApi.retrofitService.getMomoNetworks()
    }


}