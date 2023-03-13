package com.example.seerbitsdk.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.seerbitsdk.screenstate.MerchantDetailsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.seerbitsdk.models.Resource
import com.example.seerbitsdk.screenstate.CardBinState
import com.example.seerbitsdk.use_cases.GetMerchantDetailUseCase
import com.example.seerbitsdk.use_cases.CardBinUseCase

class MerchantDetailsViewModel : ViewModel() {


    private var _merchantState = mutableStateOf(MerchantDetailsState())
    val merchantState: State<MerchantDetailsState>
        get() = _merchantState



    private val merchantDetailsUseCase: GetMerchantDetailUseCase = GetMerchantDetailUseCase()

    private var _cardBinState = mutableStateOf(CardBinState())
    val cardBinState: State<CardBinState>
        get() = _cardBinState



    private val cardBinUseCase: CardBinUseCase = CardBinUseCase()

    init {
        fetchMerchantDetails()
    }
    fun clearMerchantDetails(){
        _merchantState.value = MerchantDetailsState()
    }
    fun clearCardBinState(){
        _cardBinState.value = CardBinState()
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


     fun fetchCardBin(firstSixDigits : String) {
        _cardBinState.value = CardBinState(isLoading = true)
        viewModelScope.launch (Dispatchers.Main){

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




}