package com.example.seerbitsdk.use_cases

import com.example.seerbitsdk.models.Resource
import com.example.seerbitsdk.repository.AvailableBanksRepository
import com.example.seerbitsdk.repository.CardBinRepository
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import okio.IOException
import org.json.JSONObject
import retrofit2.HttpException
import java.util.*
import java.util.concurrent.TimeoutException

class CardBinUseCase {


    private var cardBinRepository: CardBinRepository =
        CardBinRepository()

    operator fun invoke(firstSixDigits: String) = flow {
        try {
            emit(Resource.Loading())
            val apiResponse = cardBinRepository.getCardBin(firstSixDigits)

            if (apiResponse.isSuccessful) {
                val result = apiResponse.body()
                emit(Resource.Success(result))
            } else {

                val jsonObject = JSONObject(
                    Objects.requireNonNull<ResponseBody>(apiResponse.errorBody()).string()
                )
                if (apiResponse.code() == 500)
                    emit(Resource.Error(jsonObject.getString("message")))
                else
                    emit(Resource.Error(jsonObject.getString("message")))
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
