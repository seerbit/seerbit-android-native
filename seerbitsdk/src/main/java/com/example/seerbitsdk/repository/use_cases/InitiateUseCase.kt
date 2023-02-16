package com.example.seerbitsdk.repository.use_cases

import android.widget.Toast
import com.example.seerbitsdk.models.Resource
import com.example.seerbitsdk.models.TransactionDTO
import com.example.seerbitsdk.models.card.CardDTO
import com.example.seerbitsdk.models.transfer.TransferDTO
import com.example.seerbitsdk.repository.InitiateTransactionRepository
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import okio.IOException
import org.json.JSONObject
import retrofit2.HttpException
import java.util.*
import java.util.concurrent.TimeoutException

class InitiateUseCase {

    private var initiateTransactionRepository: InitiateTransactionRepository =
        InitiateTransactionRepository()

    operator fun invoke(transactionDTO: TransactionDTO) = flow {
        try {
            emit(Resource.Loading())
            when (transactionDTO) {
                is CardDTO -> {
                    val apiResponse = initiateTransactionRepository.initiateCard(transactionDTO)
                    if (apiResponse.isSuccessful) {
                        val result = apiResponse.body()
                        emit(Resource.Success(result))
                    } else {

                        val jsonObject = JSONObject(
                            Objects.requireNonNull<ResponseBody>(apiResponse.errorBody()).string()
                        )
                        if(apiResponse.code() == 500)
                        emit(Resource.Error(jsonObject.getString("error")))
                        else
                            emit(Resource.Error(jsonObject.getString("message")))
                    }
                }
                is TransferDTO -> {
                    val apiResponse = initiateTransactionRepository.initiateTransfer(transactionDTO)
                    if (apiResponse.isSuccessful) {
                        val result = apiResponse.body()
                        emit(Resource.Success(result))
                    } else {
                        val jsonObject = JSONObject(
                            Objects.requireNonNull<ResponseBody>(apiResponse.errorBody()).string()
                        )
                        if(apiResponse.code() == 500)
                            emit(Resource.Error(jsonObject.getString("error")))
                        else
                            emit(Resource.Error(jsonObject.getString("message")))
                    }
                }
            }


        } catch (e: IOException) {
            emit(Resource.Error("IO Exception: ${e.message}"))
        } catch (e: TimeoutException) {
            emit(Resource.Error("Timeout Exception: ${e.message}"))
        } catch (e: HttpException) {
            emit(Resource.Error("Http Exception: ${e.message}"))
        }

    }

    operator fun invoke(paymentReference: String) = flow {
        try {
            emit(Resource.Loading())
            val apiResponse = initiateTransactionRepository.queryTransaction(paymentReference)

            if (apiResponse.isSuccessful) {
                val result = apiResponse.body()
                emit(Resource.Success(result))
            } else {
                emit(Resource.Error("Unsuccessful Responses"))
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