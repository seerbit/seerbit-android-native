package com.example.seerbitsdk.use_cases

import android.widget.Toast
import com.example.seerbitsdk.models.Resource
import com.example.seerbitsdk.models.TransactionDTO
import com.example.seerbitsdk.models.bankaccount.BankAccountDTO
import com.example.seerbitsdk.models.card.CardDTO
import com.example.seerbitsdk.models.momo.MomoDTO
import com.example.seerbitsdk.models.transfer.TransferDTO
import com.example.seerbitsdk.models.ussd.UssdDTO
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
                        if (apiResponse.code() == 500)
                            emit(Resource.Error(jsonObject.getString("message")))
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
                        if (apiResponse.code() == 500)
                            emit(Resource.Error(jsonObject.getString("message")))
                        else
                            emit(Resource.Error(jsonObject.getString("message")))
                    }
                }

                is BankAccountDTO -> {
                    val apiResponse =
                        initiateTransactionRepository.initiateBankAccountMode(transactionDTO)
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

                is MomoDTO -> {
                    val apiResponse =
                        initiateTransactionRepository.initiateMOMO(transactionDTO)
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
        }

    }

    operator fun invoke(ussdDTO: UssdDTO) = flow {
        try {
            emit(Resource.Loading())
            val apiResponse = initiateTransactionRepository.initiateUssd(ussdDTO)
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
            emit(Resource.Error("No internet connection"))
        } catch (e: TimeoutException) {
            emit(Resource.Error("Timeout exception occurred"))
        } catch (e: HttpException) {
            emit(Resource.Error("No internet connection"))
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

                val jsonObject = JSONObject(
                    Objects.requireNonNull<ResponseBody>(apiResponse.errorBody()).string()
                )
                if (apiResponse.code() == 500)
                    emit(Resource.Error(jsonObject.getString("message")))
                if (apiResponse.code() == 404)
                    //emit(Resource.Error(jsonObject.getString("message")))
                else
                    emit(Resource.Error(jsonObject.getString("message")))
            }

        } catch (e: IOException) {
            emit(Resource.Error("No internet connection"))
        } catch (e: TimeoutException) {
            emit(Resource.Error("Timeout exception occurred"))
        } catch (e: HttpException) {
            emit(Resource.Error("No internet connection"))
        }

    }
}