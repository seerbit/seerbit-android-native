package com.example.seerbitsdk.use_cases

import android.util.Log
import com.example.seerbitsdk.models.otp.BankAccountOtpDto
import com.example.seerbitsdk.models.CardOTPDTO
import com.example.seerbitsdk.models.OtpDTO
import com.example.seerbitsdk.models.Resource
import com.example.seerbitsdk.models.otp.MomoOtpDto
import com.example.seerbitsdk.repository.OTPRepository
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import okio.IOException
import org.json.JSONObject
import retrofit2.HttpException
import java.util.*
import java.util.concurrent.TimeoutException

class OtpUseCase {

    private var otpRepository: OTPRepository =
        OTPRepository()

    operator fun invoke(otpDTO: OtpDTO) = flow {
        try {
            emit(Resource.Loading())
            when(otpDTO) {
                    is CardOTPDTO -> {
                        val apiResponse = otpRepository.sendOtp(otpDTO)

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
                    }

                is BankAccountOtpDto ->{
                    val apiResponse = otpRepository.sendOtpForBankAccount(otpDTO)

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
                }
                is MomoOtpDto ->{
                    Log.w("iswrol","$otpDTO")
                    val apiResponse = otpRepository.sendOtpMomo(otpDTO)

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
                }
            }

        } catch (e: IOException) {
            emit(Resource.Error("No internet connection"))
        } catch (e: TimeoutException) {
            emit(Resource.Error("Timeout exception occurred"))
        } catch (e: HttpException) {
            emit(Resource.Error("No internet connection"))
        } catch (e: Exception) {
            emit(Resource.Error("error occurred"))
        }

    }
}