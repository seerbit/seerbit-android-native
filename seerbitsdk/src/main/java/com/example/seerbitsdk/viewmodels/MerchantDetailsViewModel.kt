package com.example.seerbitsdk.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import com.example.seerbitsdk.models.Resource
import com.example.seerbitsdk.models.TransactionDTO
import com.example.seerbitsdk.models.card.CardDTO
import com.example.seerbitsdk.repository.use_cases.GetMerchantDetailUseCase
import com.example.seerbitsdk.repository.use_cases.InitiateUseCase
import com.example.seerbitsdk.screenstate.InitiateTransactionState
import com.example.seerbitsdk.screenstate.QueryTransactionState
import kotlinx.coroutines.withContext

class MerchantDetailsViewModel : ViewModel() {


    private var _merchantState = mutableStateOf(MerchantDetailsState())
    val merchantState: State<MerchantDetailsState>
        get() = _merchantState



    private val merchantDetailsUseCase: GetMerchantDetailUseCase = GetMerchantDetailUseCase()

    private var _initiateTransactionState = mutableStateOf(InitiateTransactionState())
    val initiateTransactionState: State<InitiateTransactionState>
        get() = _initiateTransactionState

    private val initiateTransactionUseCase: InitiateUseCase = InitiateUseCase()


    init {
        fetchMerchantDetails()
    }
    fun clearMerchantDetails(){
        _merchantState.value = MerchantDetailsState()
    }


    private fun fetchMerchantDetails() {
        _merchantState.value = MerchantDetailsState(isLoading = true)
        viewModelScope.launch (Dispatchers.Main){

                merchantDetailsUseCase().collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _merchantState.value = MerchantDetailsState(data = resource.data)
                        }

                        is Resource.Error -> {
                            _merchantState.value = MerchantDetailsState(
                                hasError = true,
                                errorMessage = resource.message
                            )
                        }
                        is Resource.Loading -> {
                            _merchantState.value = MerchantDetailsState(
                                isLoading = true
                            )
                        }
                    }
                }
            }

    }


}