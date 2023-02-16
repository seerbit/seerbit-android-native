package com.example.seerbitsdk.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seerbitsdk.models.Resource
import com.example.seerbitsdk.models.TransactionDTO
import com.example.seerbitsdk.repository.use_cases.GetMerchantDetailUseCase
import com.example.seerbitsdk.repository.use_cases.InitiateUseCase
import com.example.seerbitsdk.screenstate.InitiateTransactionState
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import com.example.seerbitsdk.screenstate.QueryTransactionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InitiateTransactionViewModel : ViewModel() {


    private var _initiateTransactionState = mutableStateOf(InitiateTransactionState())
    val initiateTransactionState: State<InitiateTransactionState>
        get() = _initiateTransactionState


    private var _queryTransactionState = mutableStateOf(QueryTransactionState())
    val queryTransactionState: State<QueryTransactionState>
        get() = _queryTransactionState

    private val initiateTransactionUseCase: InitiateUseCase = InitiateUseCase()

    fun resetTransactionState(){
        _initiateTransactionState.value = InitiateTransactionState()
    }


    fun initiateTransaction(transactionDTO: TransactionDTO) {
        _initiateTransactionState.value = InitiateTransactionState(isLoading = true)
        viewModelScope.launch(Dispatchers.Main) {
            initiateTransactionUseCase(transactionDTO).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _initiateTransactionState.value =
                            InitiateTransactionState(data = resource.data)
                    }

                    is Resource.Error -> {
                        _initiateTransactionState.value = InitiateTransactionState(
                            hasError = true,
                            errorMessage = resource.message
                        )
                    }
                    is Resource.Loading -> {
                        _initiateTransactionState.value = InitiateTransactionState(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }


    fun queryTransaction(paymentReference: String) {
        _queryTransactionState.value = QueryTransactionState(isLoading = true)
        viewModelScope.launch(Dispatchers.Main) {

            initiateTransactionUseCase(paymentReference = paymentReference).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _queryTransactionState.value = QueryTransactionState(data = resource.data)
                    }

                    is Resource.Error -> {
                        _queryTransactionState.value = QueryTransactionState(
                            hasError = true,
                            errorMessage = resource.message
                        )
                    }
                    is Resource.Loading -> {
                        _queryTransactionState.value = QueryTransactionState(
                            isLoading = true
                        )
                    }
                }
            }

        }
    }

    fun generateRandomReference() : String {
        val str = "ABCDEFGHIJKLMNOPQRSTNVabcdefghijklmnopqrstuvwxyzABCD123456789"
        var password = ""
        for (i in 1..80) {
            password += str.random()
        }
        return password
    }


}