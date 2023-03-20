package com.example.seerbitsdk.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.seerbitsdk.component.PUBLIC_KEY
import com.example.seerbitsdk.models.Resource
import com.example.seerbitsdk.models.fee.FeeDto
import com.example.seerbitsdk.screenstate.CardBinState
import com.example.seerbitsdk.screenstate.FeeState
import com.example.seerbitsdk.use_cases.GetMerchantDetailUseCase
import com.example.seerbitsdk.use_cases.CardBinUseCase
import com.example.seerbitsdk.use_cases.FeeUseCase

class MerchantDetailsViewModel : ViewModel() {


    private var _merchantState = mutableStateOf(MerchantDetailsState())
    val merchantState: State<MerchantDetailsState>
        get() = _merchantState


    private var _cardBinState = mutableStateOf(CardBinState())
    val cardBinState: State<CardBinState>
        get() = _cardBinState


    private val merchantDetailsUseCase: GetMerchantDetailUseCase = GetMerchantDetailUseCase()

    private val cardBinUseCase: CardBinUseCase = CardBinUseCase()

    init {
        fetchMerchantDetails()
    }

    fun clearCardBinState() {
        _cardBinState.value = CardBinState()
    }

    fun resetFeeState() {
        _feeState.value = FeeState()
    }


    private fun fetchMerchantDetails() {
        _merchantState.value = MerchantDetailsState(isLoading = true)
        viewModelScope.launch(Dispatchers.Main) {

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



    private val feeUseCase: FeeUseCase = FeeUseCase()
    private var _feeState = mutableStateOf(FeeState())
    val feeState: State<FeeState>
        get() = _feeState
    fun getFee(feeDto: FeeDto) {
        _feeState.value = FeeState(isLoading = true)
        viewModelScope.launch(Dispatchers.Main) {

            feeUseCase(feeDto).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _feeState.value = FeeState(data = resource.data)
                    }

                    is Resource.Error -> {
                        _feeState.value = FeeState(
                            hasError = true,
                            errorMessage = resource.message
                        )
                    }
                    is Resource.Loading -> {
                        _feeState.value = FeeState(
                            isLoading = true
                        )
                    }
                }
            }
        }

    }


    fun fetchCardBin(firstSixDigits: String) {
        _cardBinState.value = CardBinState(isLoading = true)
        viewModelScope.launch(Dispatchers.Main) {

            cardBinUseCase(firstSixDigits).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _cardBinState.value = CardBinState(data = resource.data)
                    }

                    is Resource.Error -> {
                        _cardBinState.value = CardBinState(
                            hasError = true,
                            errorMessage = resource.message
                        )
                    }
                    is Resource.Loading -> {
                        _cardBinState.value = CardBinState(
                            isLoading = true
                        )
                    }
                }
            }
        }

    }

    fun setPublicKey(publicKey : String){
        PUBLIC_KEY = publicKey
    }

}

