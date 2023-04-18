package com.example.seerbitsdk.use_cases

import com.example.seerbitsdk.models.Resource
import com.example.seerbitsdk.api.SeerBitService
import com.example.seerbitsdk.repository.SeerMerchantDetailsRepository
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import okio.IOException
import org.json.JSONObject
import retrofit2.HttpException
import java.util.*
import java.util.concurrent.TimeoutException
import javax.inject.Inject

class GetMerchantDetailUseCase {

    private var seerMerchantDetailsRepository: SeerMerchantDetailsRepository =
        SeerMerchantDetailsRepository()

    operator fun invoke(pulicKey:String) = flow {
        try {
            emit(Resource.Loading())
            val apiResponse = seerMerchantDetailsRepository.getMerchantDetails(pulicKey)

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
            emit(Resource.Error("IO exception occurred"))
        } catch (e: TimeoutException) {
            emit(Resource.Error("Timeout exception occurred"))
        } catch (e: HttpException) {
            emit(Resource.Error("No internet connection"))
        }

    }

}