package com.example.seerbitsdk.use_cases

import com.example.seerbitsdk.models.Resource
import com.example.seerbitsdk.repository.AvailableBanksRepository
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import java.util.concurrent.TimeoutException


class GetMomoNetworkUseCase {

    private var availableBanksRepository : AvailableBanksRepository =
        AvailableBanksRepository()

    operator fun invoke() = flow {
        try {
            emit(Resource.Loading())
            val apiResponse = availableBanksRepository.getMomoNetworks()

            if (apiResponse.isSuccessful) {
                val result = apiResponse.body()
                emit(Resource.Success(result))
            } else {
                emit(Resource.Error("Unsuccessful Response"))
            }

        } catch (e: IOException) {
            emit(Resource.Error("IO Exception: ${e.message}"))
        } catch (e: TimeoutException) {
            emit(Resource.Error("Timeout Exception: ${e.message}"))
        } catch (e: HttpException) {
            emit(Resource.Error("Http Exception: ${e.message}"))
        }

    }
}